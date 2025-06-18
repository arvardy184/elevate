package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.CourseApiService
import com.application.elevate.data.mapper.*
import com.application.elevate.data.repository.CourseOfflineRepository
import com.application.elevate.data.repository.CategoryOfflineRepository
import com.application.elevate.model.*
import com.application.elevate.data.database.entity.QuizAnswerEntity
import com.application.elevate.data.database.entity.QuizCompletionEntity
import com.application.elevate.util.NetworkMonitor
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import com.application.elevate.data.repository.UserRepository

@Singleton
class CourseRepository @Inject constructor(
    private val courseApiService: CourseApiService,
    private val courseOfflineRepository: CourseOfflineRepository,
    private val categoryOfflineRepository: CategoryOfflineRepository,
    private val networkMonitor: NetworkMonitor,
    private val userRepository: UserRepository
) {
    private val TAG = "CourseRepository"

    init {
        // Setup callback untuk clear quiz data saat logout
        userRepository.setClearQuizDataCallback { clearUserQuizData() }
    }

    // Helper function untuk mendapatkan user ID
    private suspend fun getCurrentUserId(): Int? {
        return userRepository.getUser()?.id
    }

    suspend fun getCourses(token: String): Result<CourseResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mengambil daftar course dari API")
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = courseApiService.getCourses(formattedToken)
                
                // Save to local database
                val courseEntities = response.courses.map { it.toEntity() }
                courseOfflineRepository.insertCourses(courseEntities)
                
                Log.d(TAG, "Berhasil mengambil daftar course dan menyimpan ke local")
                Result.success(response)
            } else {
                Log.d(TAG, "Offline - Mengambil daftar course dari local database")
                val localCourses = courseOfflineRepository.getAllCourses()
                val courses = localCourses.first()
                if (courses.isNotEmpty()) {
                    val apiCourses = courses.map { it.toApiModel() }
                    val response = CourseResponse(
                        courses = apiCourses,
                        pagination = Pagination(
                            page = 1,
                            limit = apiCourses.size,
                            total = apiCourses.size,
                            totalPages = 1
                        )
                    )
                    Result.success(response)
                } else {
                    Result.failure(Exception("Tidak ada data course tersimpan. Silakan terhubung ke internet."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil daftar course: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getCourseDetail(token: String, courseId: Int): Result<CourseDetailResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mengambil detail course dengan id: $courseId")
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = courseApiService.getCourseDetail(formattedToken, courseId)
                
                // Save to local database
                val courseDetailEntity = response.course.toEntity()
                courseOfflineRepository.insertCourseDetail(courseDetailEntity)
                
                Log.d(TAG, "Berhasil mengambil detail course dan menyimpan ke local")
                Result.success(response)
            } else {
                Log.d(TAG, "Offline - Mengambil detail course dari local database")
                val localCourseDetail = courseOfflineRepository.getCourseDetailById(courseId)
                val localCourse = courseOfflineRepository.getCourseById(courseId) // Get enrollment status dari courses table
                
                if (localCourseDetail != null) {
                    val apiCourseDetail = localCourseDetail.toApiModel()
                    
                    // PENTING: Update isEnrolled status dari courses table jika ada
                    val updatedCourseDetail = if (localCourse != null) {
                        apiCourseDetail.copy(
                            // Menggunakan isEnrolled dari tabel courses yang berisi status enrollment yang benar
                            isEnrolled = localCourse.isEnrolled
                        )
                    } else {
                        apiCourseDetail
                    }
                    
                    val response = CourseDetailResponse(course = updatedCourseDetail)
                    Log.d(TAG, "Detail course offline - isEnrolled: ${updatedCourseDetail.isEnrolled}")
                    Result.success(response)
                } else {
                    Result.failure(Exception("Detail course tidak tersedia offline. Silakan terhubung ke internet."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil detail course: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun getCategories(): Result<CategoryResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mengambil daftar categories dari API")
                val response = courseApiService.getCategories()
                
                // Save to local database
                val categoryEntities = response.categories.map { it.toEntity() }
                categoryOfflineRepository.saveCategories(categoryEntities)
                
                Log.d(TAG, "Berhasil mengambil daftar categories dan menyimpan ke local")
                Result.success(response)
            } else {
                Log.d(TAG, "Offline - Mengambil daftar categories dari local database")
                val localCategories = categoryOfflineRepository.getAllCategories()
                if (localCategories.isNotEmpty()) {
                    val apiCategories = localCategories.map { it.toApiModel() }
                    val response = CategoryResponse(categories = apiCategories)
                    Result.success(response)
                } else {
                    Result.failure(Exception("Tidak ada data kategori tersimpan. Silakan terhubung ke internet."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil daftar categories: ${e.message}")
            Result.failure(e)
        }
    }
    
    // getCourseProgress dihapus karena API belum tersedia
    
    suspend fun getCourseVideos(token: String, courseId: Int): Result<CourseVideoResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mengambil video course dengan id: $courseId")
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                
                try {
                    val response = courseApiService.getCourseVideos(formattedToken, courseId)
                    
                    // Save to local database
                    val videoEntities = response.courseVideos.map { it.toEntity() }
                    courseOfflineRepository.insertCourseVideos(videoEntities)
                    
                    Log.d(TAG, "Berhasil mengambil video course dan menyimpan ke local")
                    Result.success(response)
                } catch (e: Exception) {
                    Log.w(TAG, "Video course belum tersedia di server: ${e.message}")
                    // Return empty response for videos not available yet
                    val emptyResponse = CourseVideoResponse(courseVideos = emptyList(), note = "Video belum tersedia")
                    Result.success(emptyResponse)
                }
            } else {
                Log.d(TAG, "Offline - Mengambil video course dari local database")
                val localVideos = courseOfflineRepository.getCourseVideos(courseId)
                if (localVideos.isNotEmpty()) {
                    val apiVideos = localVideos.map { it.toApiModel() }
                    val response = CourseVideoResponse(courseVideos = apiVideos, note = "Data dari penyimpanan lokal")
                    Result.success(response)
                } else {
                    Result.failure(Exception("Video course tidak tersedia offline. Silakan terhubung ke internet."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil video course: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun getCourseQuizzes(token: String, courseId: Int): Result<CourseQuizResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mengambil quiz course dengan id: $courseId")
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                
                try {
                    val response = courseApiService.getCourseQuizzes(formattedToken, courseId)
                    
                    // Save to local database
                    val quizEntities = response.quizzes.map { it.toEntity() }
                    courseOfflineRepository.insertCourseQuizzes(quizEntities)
                    
                    Log.d(TAG, "Berhasil mengambil quiz course dan menyimpan ke local")
                    Result.success(response)
                } catch (e: Exception) {
                    Log.w(TAG, "Quiz course belum tersedia di server: ${e.message}")
                    // Return empty response for quizzes not available yet
                    val emptyResponse = CourseQuizResponse(quizzes = emptyList())
                    Result.success(emptyResponse)
                }
            } else {
                Log.d(TAG, "Offline - Mengambil quiz course dari local database")
                val localQuizzes = courseOfflineRepository.getCourseQuizzes(courseId)
                if (localQuizzes.isNotEmpty()) {
                    val apiQuizzes = localQuizzes.map { it.toApiModel() }
                    val response = CourseQuizResponse(quizzes = apiQuizzes)
                    Result.success(response)
                } else {
                    Result.failure(Exception("Quiz course tidak tersedia offline. Silakan terhubung ke internet."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil quiz course: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun submitQuiz(token: String, courseId: Int, quizId: Int, request: QuizSubmitRequest): Result<QuizSubmitResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mengirim jawaban quiz dengan id: $quizId")
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = courseApiService.submitQuiz(formattedToken, courseId, quizId, request)
                Log.d(TAG, "Berhasil mengirim jawaban quiz")
                Result.success(response)
            } else {
                Log.d(TAG, "Offline - Menyimpan jawaban quiz untuk sync nanti")
                
                val userId = getCurrentUserId()
                if (userId == null) {
                    Log.e(TAG, "User ID tidak ditemukan untuk offline quiz submission")
                    return Result.failure(Exception("User tidak ditemukan"))
                }
                
                // Save answers locally for sync later
                val answerEntities = request.answers.mapIndexed { index, answer ->
                    QuizAnswerEntity(
                        userId = userId,
                        courseId = courseId,
                        quizId = quizId,
                        questionId = index,
                        answer = answer,
                        isSynced = false
                    )
                }
                courseOfflineRepository.saveQuizAnswers(userId, courseId, answerEntities)
                
                // Return mock response for offline
                val mockResponse = QuizSubmitResponse(
                    message = "Jawaban disimpan offline. Akan dikirim saat online kembali.",
                    score = 0, // Will be calculated when synced
                    totalQuestions = request.answers.size,
                    isPassed = false // Will be determined when synced
                )
                
                Result.success(mockResponse)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengirim jawaban quiz: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun submitAllQuizzes(token: String, courseId: Int, request: QuizSubmitRequest): Result<QuizSubmitResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mengirim semua jawaban quiz untuk course: $courseId")
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                
                // Submit all answers at once using a general quiz endpoint
                // Using courseId as quizId since we're submitting all answers at once
                val response = courseApiService.submitQuiz(formattedToken, courseId, courseId, request)
                Log.d(TAG, "Berhasil mengirim semua jawaban quiz")
                Result.success(response)
            } else {
                Log.d(TAG, "Offline - Menyimpan semua jawaban quiz untuk sync nanti")
                saveQuizAnswersForSync(courseId, request.answers)
                
                // Return mock response for offline
                val mockResponse = QuizSubmitResponse(
                    message = "Semua jawaban disimpan offline. Akan dikirim saat online kembali.",
                    score = 0, // Will be calculated when synced
                    totalQuestions = request.answers.size,
                    isPassed = false // Will be determined when synced
                )
                
                Result.success(mockResponse)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengirim semua jawaban quiz: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun saveQuizAnswersForSync(courseId: Int, answers: List<String>) {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "User ID tidak ditemukan, tidak bisa menyimpan jawaban quiz")
                throw Exception("User tidak ditemukan")
            }

            Log.d(TAG, "Menyimpan ${answers.size} jawaban quiz untuk user $userId, course $courseId")
            
            // Get course quizzes to map index to quizId
            val courseQuizzes = courseOfflineRepository.getCourseQuizzes(courseId)
            val answerEntities = answers.mapIndexed { index, answer ->
                val quizzes = courseQuizzes.sortedBy { it.id } // Ensure consistent ordering
                val quiz = quizzes.getOrNull(index)
                
                QuizAnswerEntity(
                    userId = userId,
                    courseId = courseId,
                    quizId = quiz?.id ?: (index + 1), // Fallback to index+1 if no quiz found
                    questionId = quiz?.id ?: (index + 1),
                    answer = answer,
                    isSynced = false
                )
            }
            
            courseOfflineRepository.saveQuizAnswers(userId, courseId, answerEntities)
            
            Log.d(TAG, "Berhasil menyimpan jawaban quiz untuk user $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan jawaban quiz untuk sync: ${e.message}")
            throw e
        }
    }
    
    suspend fun saveQuizCompletion(courseId: Int, completion: QuizCompletionEntity) {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "User ID tidak ditemukan, tidak bisa menyimpan quiz completion")
                throw Exception("User tidak ditemukan")
            }

            courseOfflineRepository.saveQuizCompletion(userId, courseId, completion)
            Log.d(TAG, "Quiz completion saved for user $userId, course $courseId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save quiz completion for course $courseId: ${e.message}")
            throw e
        }
    }
    
    suspend fun getQuizCompletion(courseId: Int): QuizCompletionEntity? {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "User ID tidak ditemukan, tidak bisa mengambil quiz completion")
                return null
            }

            courseOfflineRepository.getQuizCompletion(userId, courseId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get quiz completion for course $courseId: ${e.message}")
            null
        }
    }
    
    suspend fun getAllQuizCompletions(): List<QuizCompletionEntity> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "User ID tidak ditemukan, tidak bisa mengambil quiz completions")
                return emptyList()
            }

            courseOfflineRepository.getAllQuizCompletions(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get all quiz completions: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun enrollCourse(token: String, courseId: Int): Result<CourseDetailResponse> {
        return try {
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (isOnline) {
                Log.d(TAG, "Online - Mendaftar course dengan id: $courseId")
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                Log.d(TAG, "Using token: ${formattedToken.take(20)}...")
                val response = courseApiService.enrollCourse(formattedToken, courseId, emptyMap())
                
                // Update local course as enrolled
                courseOfflineRepository.enrollCourseOffline(courseId)
                courseOfflineRepository.markCourseAsSynced(courseId)
                
                Log.d(TAG, "Berhasil mendaftar course")
                Result.success(response)
            } else {
                Log.d(TAG, "Offline - Menyimpan enrollment untuk sync nanti")
                
                // Save enrollment for sync later
                courseOfflineRepository.addPendingEnrollment(courseId)
                courseOfflineRepository.enrollCourseOffline(courseId)
                
                // Get local course detail for response
                val localCourseDetail = courseOfflineRepository.getCourseDetailById(courseId)
                if (localCourseDetail != null) {
                    val apiCourseDetail = localCourseDetail.toApiModel()
                    val response = CourseDetailResponse(course = apiCourseDetail)
                    Log.d(TAG, "Enrollment disimpan untuk sync nanti")
                    Result.success(response)
                } else {
                    Result.failure(Exception("Course tidak tersedia offline. Silakan terhubung ke internet."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mendaftar course: ${e.message}")
            if (e is retrofit2.HttpException) {
                Log.e(TAG, "HTTP Status Code: ${e.code()}")
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "Error response body: $errorBody")
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to read error body: ${ex.message}")
                }
            }
            Result.failure(e)
        }
    }
    
    suspend fun getVideoProxy(token: String, videoId: Int): Result<okhttp3.ResponseBody> {
        return try {
            Log.d(TAG, "Mengambil video proxy dengan id: $videoId")
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = courseApiService.getVideoProxy(formattedToken, videoId)
            Log.d(TAG, "Berhasil mengambil video proxy")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil video proxy: ${e.message}")
            Result.failure(e)
        }
    }
    

    
    // Check if course is available offline
    suspend fun isCourseAvailableOffline(courseId: Int): Boolean {
        return try {
            courseOfflineRepository.isCourseDataComplete(courseId)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking offline availability: ${e.message}")
            false
        }
    }
    
    // Sync pending data when online
    suspend fun syncPendingData(token: String): Result<Boolean> {
        return try {
            if (!networkMonitor.isCurrentlyOnline()) {
                return Result.failure(Exception("Tidak dapat sync saat offline"))
            }
            
            Log.d(TAG, "Mulai sync data yang pending")
            var syncSuccess = true
            
            // Sync pending enrollments
            val pendingEnrollments = courseOfflineRepository.getUnsyncedEnrollments()
            for (enrollment in pendingEnrollments) {
                try {
                    val enrollResult = courseApiService.enrollCourse(
                        if (token.startsWith("Bearer ")) token else "Bearer $token",
                        enrollment.courseId,
                        emptyMap()
                    )
                    courseOfflineRepository.markEnrollmentAsSynced(enrollment.courseId)
                    Log.d(TAG, "Berhasil sync enrollment course ${enrollment.courseId}")
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal sync enrollment course ${enrollment.courseId}: ${e.message}")
                    syncSuccess = false
                }
            }
            
            // Sync pending quiz answers
            val unsyncedCourses = courseOfflineRepository.getUnsyncedCourses()
            for (course in unsyncedCourses) {
                try {
                    val userId = getCurrentUserId()
                    if (userId == null) {
                        Log.e(TAG, "User ID tidak ditemukan untuk sync quiz")
                        syncSuccess = false
                        continue
                    }

                    val unsyncedAnswers = courseOfflineRepository.getUnsyncedQuizAnswers(userId, course.id)
                    if (unsyncedAnswers.isNotEmpty()) {
                        // Convert to new API format: List<String> with answer indices
                        val sortedAnswers = unsyncedAnswers.sortedBy { it.questionId }
                        val answersStringList = sortedAnswers.map { it.answer }
                        val submitRequest = QuizSubmitRequest(answers = answersStringList)
                        
                        try {
                            val response = courseApiService.submitQuiz(
                                if (token.startsWith("Bearer ")) token else "Bearer $token",
                                course.id,
                                course.id, // Using courseId as quizId for bulk submission
                                submitRequest
                            )
                            courseOfflineRepository.markQuizAnswersAsSynced(userId, course.id)
                            
                            // Update quiz completion with actual results from server
                            try {
                                val completionEntity = QuizCompletionEntity(
                                    id = QuizCompletionEntity.generateId(userId, course.id),
                                    userId = userId,
                                    courseId = course.id,
                                    score = response.score,
                                    totalQuestions = response.totalQuestions,
                                    isPassed = response.isPassed,
                                    completedAt = System.currentTimeMillis(),
                                    isSynced = true
                                )
                                courseOfflineRepository.saveQuizCompletion(userId, course.id, completionEntity)
                                Log.d(TAG, "Quiz completion updated after sync for user $userId, course ${course.id}")
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to update quiz completion after sync", e)
                            }
                            
                            Log.d(TAG, "Berhasil sync quiz answers untuk user $userId, course ${course.id}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Gagal sync quiz answers untuk course ${course.id}: ${e.message}")
                            syncSuccess = false
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing unsynced answers for course ${course.id}: ${e.message}")
                    syncSuccess = false
                }
            }
            
            Log.d(TAG, "Sync data selesai. Success: $syncSuccess")
            Result.success(syncSuccess)
        } catch (e: Exception) {
            Log.e(TAG, "Error saat sync data: ${e.message}")
            Result.failure(e)
        }
    }
    
    // Method untuk clear quiz data saat logout
    suspend fun clearUserQuizData() {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                courseOfflineRepository.clearUserQuizData(userId)
                Log.d(TAG, "Cleared quiz data for user $userId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing user quiz data: ${e.message}")
        }
    }

    suspend fun hasUnsyncedQuizAnswers(userId: Int): Boolean {
        return try {
            val unsyncedAnswers = courseOfflineRepository.getUnsyncedQuizAnswers(userId)
            unsyncedAnswers.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking unsynced quiz answers: ${e.message}")
            false
        }
    }
} 