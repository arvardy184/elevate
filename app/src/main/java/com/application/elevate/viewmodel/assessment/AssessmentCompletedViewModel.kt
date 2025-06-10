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
import com.application.elevate.data.repository.AssessmentRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.AssessmentHistory

data class AssessmentCompletedUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val assessmentHistory: AssessmentHistory? = null
)

@HiltViewModel
class AssessmentCompletedViewModel @Inject constructor(
    private val assessmentRepository: AssessmentRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val TAG = "AssessmentCompletedViewModel"

    private val _uiState = MutableStateFlow(AssessmentCompletedUiState())
    val uiState: StateFlow<AssessmentCompletedUiState> = _uiState

    init {
        fetchAssessmentHistory()
    }

    fun fetchAssessmentHistory() {
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
                
                val result = assessmentRepository.getAssessmentHistory(token)
                result.onSuccess { response ->
                    Log.d(TAG, "Assessment history fetched successfully: $response")
                    val latestAssessment = response.data.firstOrNull()
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            assessmentHistory = latestAssessment,
                            error = null
                        ) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to fetch assessment history", error)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Gagal mengambil riwayat assessment"
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching assessment history", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }
} 