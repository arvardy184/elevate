package com.application.elevate.viewmodel.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.CourseRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.util.NetworkMonitor
import com.application.elevate.model.CourseItem
import com.application.elevate.model.CourseDetailItem
import com.application.elevate.model.CategoryItem

import com.application.elevate.model.CourseVideoItem
import com.application.elevate.model.CourseQuizItem
import com.application.elevate.model.QuizSubmitRequest
import com.application.elevate.model.QuizSubmitResponse
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import com.application.elevate.worker.CourseSyncWorker
import com.application.elevate.data.mapper.toEntity
import com.application.elevate.data.mapper.toQuizCompletionData
import javax.inject.Inject

data class CourseUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val courses: List<CourseItem> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCourse: CourseDetailItem? = null,
    val selectedCourseItem: CourseItem? = null, // For getting isEnrolled status
    val recentOpenedCourse: CourseItem? = null,
    val courseVideos: List<CourseVideoItem> = emptyList(),
    val courseQuizzes: List<CourseQuizItem> = emptyList(),
    val isLoadingVideos: Boolean = false,
    val isLoadingQuizzes: Boolean = false,
    val quizSubmitResponse: QuizSubmitResponse? = null,
    val showQuizResult: Boolean = false,
    val isLoadingVideoProxy: Boolean = false,
    val videoProxyUrl: String? = null,
    val videoDownloadProgress: String? = null,
    // Offline-related states
    val isOffline: Boolean = false,
    val isCourseAvailableOffline: Boolean = false,
    val hasPendingSync: Boolean = false,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    // Quiz error and completion states
    val showQuizError: Boolean = false,
    val quizErrorMessage: String? = null,
    val completedQuizCourseIds: Set<Int> = emptySet(),
    val quizCompletionData: Map<Int, QuizCompletionData> = emptyMap(),
    // Offline quiz submission states
    val showOfflineQuizSubmitDialog: Boolean = false,
    val offlineQuizSubmitMessage: String? = null,
    val isQuizPendingSubmission: Boolean = false,
    val showQuizSuccessDialog: Boolean = false,
    val quizSuccessMessage: String? = null,
    // Flag to prevent duplicate success dialogs
    val hasShownSyncSuccessDialog: Boolean = false
)

data class QuizCompletionData(
    val score: Int,
    val totalQuestions: Int,
    val isPassed: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = "CourseViewModel"

    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState

    init {
        fetchInitialData()
        observeNetworkStatus()
        loadQuizCompletionsFromDatabase()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            fetchCourses()
            fetchCategories()
        }
    }

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali.",
                            isLoading = false
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }
                
                val result = courseRepository.getCourses(token)
                result.onSuccess { response ->
                    Log.d(TAG, "Courses fetched successfully: ${response.courses.size} courses")
                    
                    // Set recent opened course (course terakhir dalam list)
                    val recentCourse = response.courses.lastOrNull()
                    
                    // Update selectedCourseItem if it exists (untuk course detail screen)
                    val currentSelectedCourseItem = _uiState.value.selectedCourseItem
                    val updatedSelectedCourseItem = if (currentSelectedCourseItem != null) {
                        response.courses.find { it.id == currentSelectedCourseItem.id }
                    } else null
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            courses = response.courses,
                            recentOpenedCourse = recentCourse,
                            selectedCourseItem = updatedSelectedCourseItem ?: it.selectedCourseItem,
                            error = null
                        ) 
                    }
                    
                    // Note: Course progress feature removed as API is not available yet
                }.onFailure { error ->
                    Log.e(TAG, "Failed to fetch courses", error)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Gagal mengambil daftar course"
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching courses", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = courseRepository.getCategories()
                result.onSuccess { response ->
                    Log.d(TAG, "Categories fetched successfully: ${response.categories.size} categories")
                    _uiState.update { 
                        it.copy(categories = response.categories) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to fetch categories", error)
                    // Don't update error state for categories failure, keep showing courses
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching categories", e)
            }
        }
    }

    // fetchCourseProgress method removed as API is not available yet

    fun getCourseDetail(courseId: Int) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali.",
                            isLoading = false
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }
                
                val result = courseRepository.getCourseDetail(token, courseId)
                result.onSuccess { response ->
                    Log.d(TAG, "Course detail fetched successfully: ${response.course.title}")
                    
                    // Also find the corresponding CourseItem from courses list for isEnrolled status
                    val selectedCourseItem = _uiState.value.courses.find { it.id == courseId }
                    
                                    // Check if course is available offline
                val isAvailableOffline = courseRepository.isCourseAvailableOffline(courseId)
                
                // Debug log for download status
                Log.d(TAG, "Course $courseId download status - isAvailableOffline: $isAvailableOffline")
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        selectedCourse = response.course,
                        selectedCourseItem = selectedCourseItem,
                        isCourseAvailableOffline = isAvailableOffline,
                        error = null
                    ) 
                }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to fetch course detail", error)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Gagal mengambil detail course"
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching course detail", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun getCourseVideos(courseId: Int) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoadingVideos = true) }
                
                val result = courseRepository.getCourseVideos(token, courseId)
                result.onSuccess { response ->
                    Log.d(TAG, "Course videos fetched successfully: ${response.courseVideos.size} videos")
                    _uiState.update { 
                        it.copy(
                            isLoadingVideos = false,
                            courseVideos = response.courseVideos,
                            error = null
                        ) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to fetch course videos", error)
                    _uiState.update { 
                        it.copy(
                            isLoadingVideos = false,
                            courseVideos = emptyList(), // Set empty list instead of showing error
                            error = null // Don't show error for missing videos
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching course videos", e)
                _uiState.update { 
                    it.copy(
                        isLoadingVideos = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun getCourseQuizzes(courseId: Int) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoadingQuizzes = true) }
                
                val result = courseRepository.getCourseQuizzes(token, courseId)
                result.onSuccess { response ->
                    Log.d(TAG, "Course quizzes fetched successfully: ${response.quizzes.size} quizzes")
                    _uiState.update { 
                        it.copy(
                            isLoadingQuizzes = false,
                            courseQuizzes = response.quizzes,
                            error = null
                        ) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to fetch course quizzes", error)
                    _uiState.update { 
                        it.copy(
                            isLoadingQuizzes = false,
                            courseQuizzes = emptyList(), // Set empty list instead of showing error
                            error = null // Don't show error for missing quizzes
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching course quizzes", e)
                _uiState.update { 
                    it.copy(
                        isLoadingQuizzes = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun submitQuiz(courseId: Int, quizId: Int, request: QuizSubmitRequest) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }
                
                val result = courseRepository.submitQuiz(token, courseId, quizId, request)
                result.onSuccess { response ->
                    Log.d(TAG, "Quiz submitted successfully")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            quizSubmitResponse = response,
                            showQuizResult = true,
                            error = null
                        ) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to submit quiz", error)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Gagal mengirim jawaban quiz"
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting quiz", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun submitAllQuizzes(courseId: Int, request: QuizSubmitRequest) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            showQuizError = true,
                            quizErrorMessage = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }
                
                // Check if device is online first
                val isOnline = networkMonitor.isCurrentlyOnline()
                
                if (isOnline) {
                    // Online submission - submit directly to API
                    Log.d(TAG, "Device is online - submitting quiz directly to API")
                    
                    // Reset sync success flag for new quiz submission
                    resetSyncSuccessFlag()
                    
                    val result = courseRepository.submitAllQuizzes(token, courseId, request)
                    result.onSuccess { response ->
                        Log.d(TAG, "All quizzes submitted successfully")
                        
                        // Trigger immediate sync worker
                        CourseSyncWorker.enqueue(context)
                        
                        // Mark quiz as completed for this course
                        val completionData = QuizCompletionData(
                            score = response.score,
                            totalQuestions = response.totalQuestions,
                            isPassed = response.isPassed
                        )
                        
                        // Save completion to database
                        try {
                            val user = userRepository.getUser()
                            val userId = user?.id
                            if (userId == null) {
                                Log.e(TAG, "User ID tidak ditemukan untuk quiz completion")
                            } else {
                                val completionEntity = completionData.toEntity(courseId, userId)
                                courseRepository.saveQuizCompletion(courseId, completionEntity)
                                Log.d(TAG, "Quiz completion saved to database for user $userId, course $courseId")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save quiz completion to database", e)
                        }
                        
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                quizSubmitResponse = response,
                                showQuizResult = true,
                                completedQuizCourseIds = currentState.completedQuizCourseIds + courseId,
                                quizCompletionData = currentState.quizCompletionData + (courseId to completionData),
                                error = null
                            ) 
                        }
                    }.onFailure { error ->
                        Log.e(TAG, "Failed to submit all quizzes", error)
                        val errorMessage = parseQuizErrorMessage(error)
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                showQuizError = true,
                                quizErrorMessage = errorMessage
                            ) 
                        }
                    }
                } else {
                    // Offline - Show dialog and save for later sync
                    Log.d(TAG, "Device is offline - saving quiz answers for later sync")
                    
                    // Reset sync success flag for new quiz submission
                    resetSyncSuccessFlag()
                    
                    // Show offline dialog
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showOfflineQuizSubmitDialog = true,
                            offlineQuizSubmitMessage = "Anda sedang offline. Jawaban quiz akan dikirim otomatis saat kembali online.",
                            isQuizPendingSubmission = true
                        ) 
                    }
                    
                    try {
                        courseRepository.saveQuizAnswersForSync(courseId, request.answers)
                        Log.d(TAG, "Quiz answers saved for offline sync")
                        
                        // Save offline quiz completion (will be updated when synced)
                        val offlineCompletionData = QuizCompletionData(
                            score = 0, // Will be calculated when synced
                            totalQuestions = request.answers.size,
                            isPassed = false // Will be determined when synced
                        )
                        
                        try {
                            val user = userRepository.getUser()
                            val userId = user?.id
                            if (userId == null) {
                                Log.e(TAG, "User ID tidak ditemukan untuk offline quiz completion")
                            } else {
                                val completionEntity = offlineCompletionData.toEntity(courseId, userId)
                                courseRepository.saveQuizCompletion(courseId, completionEntity)
                                Log.d(TAG, "Offline quiz completion saved for user $userId, course $courseId")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save offline quiz completion", e)
                        }
                        
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                showOfflineQuizSubmitDialog = true,
                                offlineQuizSubmitMessage = "Jawaban quiz akan dikirim secara otomatis ketika terhubung ke internet.",
                                isQuizPendingSubmission = true,
                                completedQuizCourseIds = currentState.completedQuizCourseIds + courseId,
                                quizCompletionData = currentState.quizCompletionData + (courseId to offlineCompletionData),
                                error = null
                            ) 
                        }
                    } catch (saveError: Exception) {
                        Log.e(TAG, "Failed to save quiz answers for sync", saveError)
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                showQuizError = true,
                                quizErrorMessage = "Gagal menyimpan jawaban quiz: ${saveError.message}"
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting all quizzes", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        showQuizError = true,
                        quizErrorMessage = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    private fun parseQuizErrorMessage(error: Throwable): String {
        return when {
            error is retrofit2.HttpException -> {
                when (error.code()) {
                    400 -> {
                        try {
                            val errorBody = error.response()?.errorBody()?.string()
                            if (!errorBody.isNullOrEmpty()) {
                                val gson = com.google.gson.Gson()
                                val apiError = gson.fromJson(errorBody, com.application.elevate.model.ApiErrorResponse::class.java)
                                apiError?.message ?: apiError?.error ?: "Quiz sudah pernah dikerjakan atau ada masalah dengan jawaban yang dikirim"
                            } else {
                                "Quiz sudah pernah dikerjakan atau ada masalah dengan jawaban yang dikirim"
                            }
                        } catch (e: Exception) {
                            "Quiz sudah pernah dikerjakan atau ada masalah dengan jawaban yang dikirim"
                        }
                    }
                    401 -> "Sesi anda telah berakhir. Silakan login kembali"
                    403 -> "Anda tidak memiliki akses untuk mengerjakan quiz ini"
                    404 -> "Quiz tidak ditemukan"
                    409 -> "Quiz sudah pernah dikerjakan sebelumnya"
                    422 -> "Data quiz yang dikirim tidak valid. Pastikan semua pertanyaan terjawab"
                    500 -> "Server sedang bermasalah. Coba lagi dalam beberapa menit"
                    else -> "Terjadi kesalahan pada server (${error.code()})"
                }
            }
            error.message?.contains("timeout", ignoreCase = true) == true -> 
                "Koneksi timeout. Periksa koneksi internet dan coba lagi"
            error.message?.contains("network", ignoreCase = true) == true -> 
                "Masalah koneksi internet. Periksa koneksi dan coba lagi"
            else -> error.message ?: "Gagal mengirim jawaban quiz"
        }
    }

    fun enrollCourse(courseId: Int) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }
                
                val result = courseRepository.enrollCourse(token, courseId)
                result.onSuccess { response ->
                    Log.d(TAG, "Course enrolled successfully")
                    
                    // Trigger immediate sync worker to ensure all data is fresh
                    CourseSyncWorker.enqueue(context)
                    
                    // Update selected course detail to reflect enrollment
                    val updatedCourse = response.course
                    
                    // Auto-download course data after enrollment
                    Log.d(TAG, "Auto-downloading course data after enrollment")
                    
                    // Fetch course detail and save to local
                    getCourseDetail(courseId)
                    
                    // Fetch and save videos/quizzes
                    getCourseVideos(courseId)
                    getCourseQuizzes(courseId)
                    
                    // Check course availability after auto-download
                    viewModelScope.launch {
                        try {
                            // Wait a bit for all data to be saved
                            kotlinx.coroutines.delay(1000)
                            
                            // Check if course is now available offline
                            val isAvailableOffline = courseRepository.isCourseAvailableOffline(courseId)
                            Log.d(TAG, "Course availability after auto-save: $isAvailableOffline")
                            
                            // Update UI to reflect downloaded status
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isCourseAvailableOffline = isAvailableOffline
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to check course availability: ${e.message}")
                        }
                    }
                    
                    // Fetch ulang courses untuk mendapatkan status isEnrolled yang updated
                    fetchCourses()
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            selectedCourse = updatedCourse, // Update immediately
                            error = null
                        ) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to enroll course", error)
                    val errorMessage = when {
                        error is retrofit2.HttpException && error.code() == 400 -> 
                            "Course sudah terdaftar atau ada masalah dengan permintaan"
                        error is retrofit2.HttpException && error.code() == 401 -> 
                            "Sesi anda telah berakhir. Silakan login kembali"
                        error is retrofit2.HttpException && error.code() == 403 -> 
                            "Anda tidak memiliki akses untuk mendaftar course ini"
                        error is retrofit2.HttpException && error.code() == 404 -> 
                            "Course tidak ditemukan"
                        else -> error.message ?: "Gagal mendaftar course"
                    }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error enrolling course", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun playVideo(videoId: Int) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoadingVideoProxy = true) }
                
                val result = courseRepository.getVideoProxy(token, videoId)
                result.onSuccess { responseBody ->
                    Log.d(TAG, "Video proxy fetched successfully for video: $videoId")
                    
                    // Save video to temporary file and open with video player
                    withContext(Dispatchers.IO) {
                        try {
                            val tempFile = File(context.cacheDir, "video_$videoId.mp4")
                            
                            // Update UI with downloading status
                            withContext(Dispatchers.Main) {
                                _uiState.update { 
                                    it.copy(videoDownloadProgress = "Mengunduh video...")
                                }
                            }
                            
                            // Write video data to temporary file
                            FileOutputStream(tempFile).use { fos ->
                                responseBody.byteStream().use { inputStream ->
                                    inputStream.copyTo(fos)
                                }
                            }
                            
                            Log.d(TAG, "Video saved to temporary file: ${tempFile.absolutePath}")
                            
                            // Create content URI using FileProvider
                            val videoUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                tempFile
                            )
                            
                            // Create intent to play video
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(videoUri, "video/*")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            
                            // Start video player
                            withContext(Dispatchers.Main) {
                                try {
                                    context.startActivity(intent)
                                    Log.d(TAG, "Video player started successfully")
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed to start video player", e)
                                    _uiState.update { 
                                        it.copy(
                                            isLoadingVideoProxy = false,
                                            videoDownloadProgress = null,
                                            error = "Tidak ada aplikasi video player yang tersedia"
                                        ) 
                                    }
                                }
                            }
                            
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save video file", e)
                            withContext(Dispatchers.Main) {
                                _uiState.update { 
                                    it.copy(
                                        isLoadingVideoProxy = false,
                                        videoDownloadProgress = null,
                                        error = "Gagal menyimpan video: ${e.message}"
                                    ) 
                                }
                            }
                        }
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isLoadingVideoProxy = false,
                            videoDownloadProgress = null,
                            error = null
                        ) 
                    }
                    
                }.onFailure { error ->
                    Log.e(TAG, "Failed to fetch video proxy", error)
                    _uiState.update { 
                        it.copy(
                            isLoadingVideoProxy = false,
                            videoDownloadProgress = null,
                            error = error.message ?: "Gagal memuat video"
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching video proxy", e)
                _uiState.update { 
                    it.copy(
                        isLoadingVideoProxy = false,
                        videoDownloadProgress = null,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun dismissQuizResult() {
        _uiState.update { it.copy(showQuizResult = false, quizSubmitResponse = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun dismissQuizError() {
        _uiState.update { it.copy(showQuizError = false, quizErrorMessage = null) }
    }
    
    fun dismissOfflineQuizSubmitDialog() {
        _uiState.update { it.copy(showOfflineQuizSubmitDialog = false, offlineQuizSubmitMessage = null) }
    }
    
    fun dismissQuizSuccessDialog() {
        _uiState.update { it.copy(showQuizSuccessDialog = false, quizSuccessMessage = null) }
    }
    
    fun resetSyncSuccessFlag() {
        _uiState.update { it.copy(hasShownSyncSuccessDialog = false) }
    }
    
    fun isQuizCompletedForCourse(courseId: Int): Boolean {
        return _uiState.value.completedQuizCourseIds.contains(courseId)
    }
    
    fun getQuizCompletionData(courseId: Int): QuizCompletionData? {
        return _uiState.value.quizCompletionData[courseId]
    }
    
    private fun loadQuizCompletionsFromDatabase() {
        viewModelScope.launch {
            try {
                val completions = courseRepository.getAllQuizCompletions()
                val completionMap = mutableMapOf<Int, QuizCompletionData>()
                val completedCourseIds = mutableSetOf<Int>()
                
                for (completion in completions) {
                    completionMap[completion.courseId] = completion.toQuizCompletionData()
                    completedCourseIds.add(completion.courseId)
                }
                
                _uiState.update { currentState ->
                    currentState.copy(
                        completedQuizCourseIds = completedCourseIds,
                        quizCompletionData = completionMap
                    )
                }
                
                Log.d(TAG, "Loaded ${completions.size} quiz completions from database")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load quiz completions from database", e)
            }
        }
    }
    
    // Offline functionality methods
    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.isOnline().collect { isOnline ->
                _uiState.update { 
                    it.copy(isOffline = !isOnline) 
                }
                
                // If online, trigger automatic sync
                if (isOnline) {
                    Log.d(TAG, "Device is now online - triggering automatic sync")
                    
                    // Trigger immediate sync worker for quiz answers and enrollments
                    CourseSyncWorker.enqueue(context)
                    
                    // Auto-sync pending data
                    syncPendingData()
                    
                    // Refresh quiz completions after sync
                    loadQuizCompletionsFromDatabase()
                    
                    checkPendingSync()
                }
            }
        }
    }
    
    private suspend fun checkPendingSync() {
        // Check if there are pending data to sync
        // This is a simplified check - in real implementation you might want to check specific conditions
        _uiState.update { 
            it.copy(hasPendingSync = false) // Will be determined by actual pending data
        }
    }
    

    
    fun checkCourseOfflineAvailability(courseId: Int) {
        viewModelScope.launch {
            try {
                val isAvailable = courseRepository.isCourseAvailableOffline(courseId)
                _uiState.update { 
                    it.copy(isCourseAvailableOffline = isAvailable) 
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking course offline availability", e)
            }
        }
    }
    
    fun syncPendingData() {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            syncMessage = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }
                
                // Check if there are pending quiz submissions before sync
                val currentUserId = getCurrentUserId()
                val hadPendingQuizSubmissions = currentUserId?.let { userId ->
                    courseRepository.hasUnsyncedQuizAnswers(userId)
                } ?: false
                
                // If no pending submissions or already shown success dialog, skip
                if (!hadPendingQuizSubmissions || _uiState.value.hasShownSyncSuccessDialog) {
                    Log.d(TAG, "No pending quiz submissions or success dialog already shown, skipping sync dialog")
                    // Still do the sync but don't show success dialog
                    val result = courseRepository.syncPendingData(token)
                    result.onSuccess { success ->
                        _uiState.update { 
                            it.copy(
                                isSyncing = false,
                                hasPendingSync = !success,
                                isQuizPendingSubmission = false
                            ) 
                        }
                        
                        if (success) {
                            fetchCourses()
                            loadQuizCompletionsFromDatabase()
                        }
                    }
                    return@launch
                }
                
                _uiState.update { 
                    it.copy(
                        isSyncing = true,
                        syncMessage = "Menyinkronkan data..."
                    ) 
                }
                
                val result = courseRepository.syncPendingData(token)
                result.onSuccess { success ->
                    val message = if (success) {
                        "Data berhasil disinkronkan"
                    } else {
                        "Beberapa data gagal disinkronkan"
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isSyncing = false,
                            syncMessage = message,
                            hasPendingSync = !success,
                            isQuizPendingSubmission = false,
                            // Show success dialog only once if there were pending quiz submissions
                            showQuizSuccessDialog = success,
                            quizSuccessMessage = if (success) 
                                "Jawaban quiz telah berhasil dikirim!" 
                            else null,
                            // Mark that we've shown the success dialog
                            hasShownSyncSuccessDialog = success
                        ) 
                    }
                    
                    // Refresh courses and quiz completions after sync
                    if (success) {
                        fetchCourses()
                        loadQuizCompletionsFromDatabase()
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to sync pending data", error)
                    _uiState.update { 
                        it.copy(
                            isSyncing = false,
                            syncMessage = error.message ?: "Gagal menyinkronkan data"
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing pending data", e)
                _uiState.update { 
                    it.copy(
                        isSyncing = false,
                        syncMessage = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun clearSyncMessage() {
        _uiState.update { it.copy(syncMessage = null) }
    }
    
    // Method untuk handle enroll saat offline - updated version
    fun enrollCourseWithOfflineSupport(courseId: Int) {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali."
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }
                
                val isOnline = !_uiState.value.isOffline
                
                if (!isOnline) {
                    // Show offline enrollment message
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            hasPendingSync = true,
                            syncMessage = "Pendaftaran course akan diproses saat online kembali."
                        ) 
                    }
                }
                
                val result = courseRepository.enrollCourse(token, courseId)
                result.onSuccess { response ->
                    Log.d(TAG, "Course enrolled successfully")
                    
                    if (isOnline) {
                        // Fetch ulang courses untuk mendapatkan status isEnrolled yang updated
                        fetchCourses()
                        
                        // Fetch ulang video dan quiz setelah enroll
                        getCourseVideos(courseId)
                        getCourseQuizzes(courseId)
                    } else {
                        // Update local state for offline enrollment
                        _uiState.update { 
                            it.copy(hasPendingSync = true) 
                        }
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = null
                        ) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to enroll course", error)
                    val errorMessage = when {
                        error is retrofit2.HttpException && error.code() == 400 -> 
                            "Course sudah terdaftar atau ada masalah dengan permintaan"
                        error is retrofit2.HttpException && error.code() == 401 -> 
                            "Sesi anda telah berakhir. Silakan login kembali"
                        error is retrofit2.HttpException && error.code() == 403 -> 
                            "Anda tidak memiliki akses untuk mendaftar course ini"
                        error is retrofit2.HttpException && error.code() == 404 -> 
                            "Course tidak ditemukan"
                        else -> error.message ?: "Gagal mendaftar course"
                    }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error enrolling course", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    private suspend fun getCurrentUserId(): Int? {
        return userRepository.getUser()?.id
    }
} 