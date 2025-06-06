package com.application.elevate.viewmodel.assessment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.application.elevate.model.AssessmentRequest
import com.application.elevate.data.repository.AuthRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.ui.assessment.AssessmentUiState
@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val TAG = "AssessmentViewModel"

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState

    // State untuk navigasi
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    // Map untuk menyimpan jawaban assessment
    private val answers = mutableMapOf<String, String>()

    init {
        checkAssessmentStatus()
    }

    private fun checkAssessmentStatus() {
        viewModelScope.launch {
            val user = userRepository.getUser()
            if (user?.isAssessmentCompleted == true) {
                // Jika assessment sudah selesai, langsung navigasi ke HomeScreen
                _navigationEvent.value = NavigationEvent.NavigateToHome
            }
        }
    }

    fun onStudentStatusChange(value: String) {
        Log.d(TAG, "Updating studentStatus to $value")
        _uiState.update { it.copy(studentStatus = value) }
    }

    fun onMajorChange(value: String) {
        Log.d(TAG, "Updating major to $value")
        _uiState.update { it.copy(major = value) }
    }

    fun onSemesterChange(value: String) {
        Log.d(TAG, "Updating semester to $value")
        _uiState.update { it.copy(semester = value) }
    }

    fun onCurrentFieldChange(value: String) {
        Log.d(TAG, "Updating currentField to $value")
        _uiState.update { it.copy(currentField = value) }
    }

    fun onInterestedFieldChange(value: String) {
        Log.d(TAG, "Updating interestedField to $value")
        _uiState.update { it.copy(interestedField = value) }
    }

    fun onDreamJobChange(value: String) {
        Log.d(TAG, "Updating dreamJob to $value")
        _uiState.update { it.copy(dreamJob = value) }
    }

    fun onGoalChange(value: String) {
        Log.d(TAG, "Updating goal to $value")
        _uiState.update { it.copy(goal = value) }
    }

    fun getAnswerForStep(key: String): String {
        return answers[key] ?: ""
    }

    fun updateAnswerForStep(key: String, value: String) {
        answers[key] = value
        when (key) {
            "studentStatus" -> onStudentStatusChange(value)
            "major" -> onMajorChange(value)
            "semester" -> onSemesterChange(value)
            "currentField" -> onCurrentFieldChange(value)
            "interestedField" -> onInterestedFieldChange(value)
            "dreamJob" -> onDreamJobChange(value)
            "goal" -> onGoalChange(value)
        }
    }

    fun setError(message: String) {
        _uiState.update { 
            it.copy(
                error = message,
                isLoading = false,
                isSuccess = false
            ) 
        }
    }

    fun clearError() {
        _uiState.update { 
            it.copy(
                error = null
            ) 
        }
    }

    fun submitAssessment() {
        viewModelScope.launch {
            try {
                val token = userRepository.getAuthToken()
                if (token == null) {
                    Log.e(TAG, "Token is null")
                    setError("Sesi anda telah berakhir. Silakan login kembali.")
                    return@launch
                }

                Log.d(TAG, "Starting assessment submission")
                _uiState.update { it.copy(isLoading = true) }

                val request = AssessmentRequest(
                    studentStatus = uiState.value.studentStatus,
                    majorStudy = uiState.value.major,
                    currentSemester = uiState.value.semester,
                    currentField = uiState.value.currentField,
                    interestedField = uiState.value.interestedField,
                    dreamJob = uiState.value.dreamJob,
                    mainGoal = uiState.value.goal
                )
                
                Log.d(TAG, "Submitting assessment with request: $request")
                repository.submitAssessment(token, request).collect { result ->
                    result.onSuccess { response ->
                        Log.d(TAG, "Assessment submission successful: $response")
                        // Update status assessment di user
                        val user = userRepository.getUser()
                        user?.let { currentUser ->
                            userRepository.updateUser(currentUser.copy(isAssessmentCompleted = true))
                        }
                        
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isAssessmentCompleted = true,
                                message = response.message
                            ) 
                        }
                        
                        // Navigasi ke HomeScreen setelah assessment selesai
                        _navigationEvent.value = NavigationEvent.NavigateToHome
                    }.onFailure { error ->
                        Log.e(TAG, "Assessment submission failed", error)
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                error = error.message ?: "Gagal mengirim assessment"
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during assessment submission", e)
                setError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    // Reset navigation event setelah digunakan
    fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
}