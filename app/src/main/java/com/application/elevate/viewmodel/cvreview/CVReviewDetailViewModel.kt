package com.application.elevate.viewmodel.cvreview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.mapper.toSimpleCVReviewData
import com.application.elevate.data.repository.CVReviewRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.CVReviewData
import com.application.elevate.util.NetworkUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CVReviewDetailViewModel @Inject constructor(
  private val cvReviewRepository: CVReviewRepository,
  private val userRepository: UserRepository,
  private val networkUtil: NetworkUtil
) : ViewModel() {

  private val _uiState = MutableStateFlow(CVReviewDetailUiState())
  val uiState: StateFlow<CVReviewDetailUiState> = _uiState.asStateFlow()

  fun loadCVReviewDetail(reviewId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      
      val isOnline = networkUtil.isOnline()
      _uiState.value = _uiState.value.copy(isOffline = !isOnline)
      
      if (isOnline) {
        // Online: Try API first
        loadFromAPI(reviewId)
      } else {
        // Offline: Load dari Room
        loadFromLocal(reviewId)
      }
    }
  }
  
  private suspend fun loadFromAPI(reviewId: String) {
    try {
      val token = userRepository.getAuthToken()
      
      if (token == null) {
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Session expired. Please login again."
        )
        return
      }
      
      cvReviewRepository.getCVReviewById(token, reviewId)
        .onSuccess { response ->
          Log.d("CVReviewDetailVM", "Loaded CV review detail successfully from API")
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            cvReviewData = response.data,
            isOffline = false
          )
        }
        .onFailure { exception ->
          Log.e("CVReviewDetailVM", "API failed, trying local data", exception)
          // Fallback ke local data
          loadFromLocal(reviewId)
        }
    } catch (e: Exception) {
      Log.e("CVReviewDetailVM", "Error getting token", e)
      // Fallback ke local data
      loadFromLocal(reviewId)
    }
  }
  
  private suspend fun loadFromLocal(reviewId: String) {
    try {
      val localEntity = cvReviewRepository.getCVReviewByIdLocal(reviewId)
      
      if (localEntity != null) {
        // Convert entity to CVReviewData (simplified version)
        val localData = localEntity.toSimpleCVReviewData()
        
        Log.d("CVReviewDetailVM", "Loaded CV review detail from local database")
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          cvReviewData = localData,
          isOffline = true
        )
      } else {
        Log.w("CVReviewDetailVM", "CV review not found in local database")
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "CV review not found. Please sync data when online.",
          isOffline = true
        )
      }
    } catch (e: Exception) {
      Log.e("CVReviewDetailVM", "Failed to load from local database", e)
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        error = "Failed to load CV review details: ${e.message}",
        isOffline = true
      )
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
  val successMessage: String? = null,
  val isOffline: Boolean = false
) 