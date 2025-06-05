package com.application.elevate.viewmodel.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.CourseRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.CourseItem
import com.application.elevate.model.CourseDetailItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val courses: List<CourseItem> = emptyList(),
    val selectedCourse: CourseDetailItem? = null
)

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val TAG = "CourseViewModel"

    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState

    init {
        fetchCourses()
    }

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val result = courseRepository.getCourses()
                result.onSuccess { response ->
                    Log.d(TAG, "Courses fetched successfully: ${response.courses.size} courses")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            courses = response.courses,
                            error = null
                        ) 
                    }
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
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            selectedCourse = response.course,
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 