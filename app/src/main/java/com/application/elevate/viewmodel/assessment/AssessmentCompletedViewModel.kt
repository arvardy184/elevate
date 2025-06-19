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
import com.application.elevate.data.repository.AssessmentOfflineRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.AssessmentHistory

data class AssessmentCompletedUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val assessmentHistory: AssessmentHistory? = null
)

@HiltViewModel
class AssessmentCompletedViewModel @Inject constructor(
    private val assessmentOfflineRepository: AssessmentOfflineRepository,
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
                val user = userRepository.getUser()
                if (user == null) {
                    _uiState.update { 
                        it.copy(
                            error = "Sesi anda telah berakhir. Silakan login kembali.",
                            isLoading = false
                        ) 
                    }
                    return@launch
                }

                val userId = user.id
                if (userId == null) {
                    _uiState.update { 
                        it.copy(
                            error = "User ID tidak ditemukan. Silakan login kembali.",
                            isLoading = false
                        ) 
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true, error = null) }
                
                Log.d(TAG, "Fetching assessment history offline-first for user: $userId")
                
                // Menggunakan offline-first approach
                assessmentOfflineRepository.getLatestAssessmentOfflineFirst(userId).collect { result ->
                    result.onSuccess { latestAssessment ->
                        Log.d(TAG, "Assessment history fetched successfully: $latestAssessment")
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

    fun retryFetchAssessment() {
        fetchAssessmentHistory()
    }
} 