package com.application.elevate.viewmodel.assessment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.application.elevate.data.repository.AssessmentRepositoryInterface
import com.application.elevate.data.repository.AssessmentOfflineRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.AssessmentHistory

data class MyAssessmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val assessmentHistory: AssessmentHistory? = null,
    val hasAssessment: Boolean = false
)

@HiltViewModel
class MyAssessmentViewModel @Inject constructor(
    private val assessmentRepository: AssessmentRepositoryInterface,
    private val assessmentOfflineRepository: AssessmentOfflineRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val TAG = "MyAssessmentViewModel"

    private val _uiState = MutableStateFlow(MyAssessmentUiState())
    val uiState: StateFlow<MyAssessmentUiState> = _uiState

    init {
        fetchAssessmentData()
    }

    fun fetchAssessmentData() {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser()
                val userId = user?.id
                if (userId == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali.",
                            isLoading = false
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Use offline-first approach
                assessmentOfflineRepository.getLatestAssessmentOfflineFirst(userId).collect { result ->
                    result.onSuccess { latestAssessment ->
                        Log.d(TAG, "Assessment data fetched successfully (offline-first): $latestAssessment")
                        
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                assessmentHistory = latestAssessment,
                                hasAssessment = latestAssessment != null,
                                error = null
                            ) 
                        }
                    }.onFailure { error ->
                        Log.e(TAG, "Failed to fetch assessment data", error)
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Gagal mengambil data assessment"
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching assessment data", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun onEditAssessment() {
        // Fungsi ini akan diimplementasi nanti
        Log.d(TAG, "Edit assessment clicked - will be implemented later")
    }

    fun retryLoadData() {
        fetchAssessmentData()
    }
} 