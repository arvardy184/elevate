package com.application.elevate.viewmodel.cvreview

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
import javax.inject.Inject

@HiltViewModel
class CVReviewDetailViewModel @Inject constructor(
  private val cvReviewRepository: CVReviewRepository,
  private val userRepository: UserRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(CVReviewDetailUiState())
  val uiState: StateFlow<CVReviewDetailUiState> = _uiState.asStateFlow()

  fun loadCVReviewDetail(reviewId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      
      try {
        val token = userRepository.getAuthToken()
        
        if (token == null) {
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = "Session expired. Please login again."
          )
          return@launch
        }
        
        cvReviewRepository.getCVReviewById(token, reviewId)
          .onSuccess { response ->
            Log.d("CVReviewDetailVM", "Loaded CV review detail successfully")
            _uiState.value = _uiState.value.copy(
              isLoading = false,
              cvReviewData = response.data
            )
          }
          .onFailure { exception ->
            Log.e("CVReviewDetailVM", "Failed to load CV review detail", exception)
            _uiState.value = _uiState.value.copy(
              isLoading = false,
              error = exception.message ?: "Failed to load CV review detail"
            )
          }
      } catch (e: Exception) {
        Log.e("CVReviewDetailVM", "Error getting token", e)
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Failed to get authentication token"
        )
      }
    }
  }

  fun updateCareerField(reviewId: String, newCareerField: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isUpdating = true, error = null)
      
      try {
        val token = userRepository.getAuthToken()
        
        if (token == null) {
          _uiState.value = _uiState.value.copy(
            isUpdating = false,
            error = "Session expired. Please login again."
          )
          return@launch
        }
        
        cvReviewRepository.updateCVReview(token, reviewId, newCareerField)
          .onSuccess { response ->
            Log.d("CVReviewDetailVM", "Updated career field successfully: ${response.message}")
            
            // Update the local data
            val updatedData = _uiState.value.cvReviewData?.copy(
              careerField = newCareerField
            )
            
            _uiState.value = _uiState.value.copy(
              isUpdating = false,
              cvReviewData = updatedData,
              successMessage = "Career field berhasil diupdate!"
            )
          }
          .onFailure { exception ->
            Log.e("CVReviewDetailVM", "Failed to update career field", exception)
            _uiState.value = _uiState.value.copy(
              isUpdating = false,
              error = exception.message ?: "Failed to update career field"
            )
          }
      } catch (e: Exception) {
        Log.e("CVReviewDetailVM", "Error updating career field", e)
        _uiState.value = _uiState.value.copy(
          isUpdating = false,
          error = "Failed to update career field"
        )
      }
    }
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }

  fun clearSuccessMessage() {
    _uiState.value = _uiState.value.copy(successMessage = null)
  }
}

data class CVReviewDetailUiState(
  val isLoading: Boolean = false,
  val isUpdating: Boolean = false,
  val cvReviewData: CVReviewData? = null,
  val error: String? = null,
  val successMessage: String? = null
) 