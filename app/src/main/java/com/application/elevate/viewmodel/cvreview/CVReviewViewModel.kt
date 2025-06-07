package com.application.elevate.ui.cvreview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.CVReviewRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.CVReviewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CVReviewViewModel @Inject constructor(
  private val cvReviewRepository: CVReviewRepository,
  private val userRepository: UserRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(CVReviewUiState())
  val uiState: StateFlow<CVReviewUiState> = _uiState.asStateFlow()

  // Method dengan token parameter (keep for backward compatibility)
  fun uploadCV(
    token: String,
    cvFile: File,
    careerField: String
  ) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      
      Log.d("CVReviewVM", "Starting CV upload...")
      
      cvReviewRepository.uploadCV(token, cvFile, careerField)
        .onSuccess { response ->
          Log.d("CVReviewVM", "Upload successful: ${response.message}")
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            cvReviewData = response.data,
            isUploadSuccess = true
          )
        }
        .onFailure { exception ->
          Log.e("CVReviewVM", "Upload failed", exception)
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = exception.message ?: "Upload gagal, coba lagi ya!"
          )
        }
    }
  }

  // Method yang lebih simple - otomatis ambil token dari UserRepository
  fun uploadCV(
    cvFile: File,
    careerField: String
  ) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      
      try {
        Log.d("CVReviewVM", "Getting auth token...")
        val token = userRepository.getAuthToken()
        
        if (token == null) {
          Log.e("CVReviewVM", "No auth token found")
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = "Session expired. Please login again."
          )
          return@launch
        }
        
        Log.d("CVReviewVM", "Starting CV upload with auto token...")
        
        cvReviewRepository.uploadCV(token, cvFile, careerField)
          .onSuccess { response ->
            Log.d("CVReviewVM", "Upload successful: ${response.message}")
            _uiState.value = _uiState.value.copy(
              isLoading = false,
              cvReviewData = response.data,
              isUploadSuccess = true
            )
          }
          .onFailure { exception ->
            Log.e("CVReviewVM", "Upload failed", exception)
            _uiState.value = _uiState.value.copy(
              isLoading = false,
              error = exception.message ?: "Upload gagal, coba lagi ya!"
            )
          }
      } catch (e: Exception) {
        Log.e("CVReviewVM", "Error getting token", e)
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Failed to get authentication token"
        )
      }
    }
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }

  fun resetUploadState() {
    _uiState.value = CVReviewUiState()
  }
}

data class CVReviewUiState(
  val isLoading: Boolean = false,
  val cvReviewData: CVReviewData? = null,
  val isUploadSuccess: Boolean = false,
  val error: String? = null
) 