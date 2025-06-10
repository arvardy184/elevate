package com.application.elevate.ui.cvreview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.CVReviewRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.CVReviewData
import com.application.elevate.util.NetworkUtil
import com.application.elevate.data.database.entity.CVReviewEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CVReviewViewModel @Inject constructor(
  private val cvReviewRepository: CVReviewRepository,
  private val userRepository: UserRepository,
  private val networkUtil: NetworkUtil
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

  // Method yang lebih simple - otomatis ambil token dari UserRepository dengan offline support
  fun uploadCV(
    cvFile: File,
    careerField: String
  ) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      
      val isOnline = networkUtil.isOnline()
      
      if (isOnline) {
        // Online: Upload langsung ke server
        uploadToServer(cvFile, careerField)
      } else {
        // Offline: Save ke queue untuk upload nanti
        saveToOfflineQueue(cvFile, careerField)
      }
    }
  }
  
  private suspend fun uploadToServer(cvFile: File, careerField: String) {
    try {
      Log.d("CVReviewVM", "Getting auth token...")
      val token = userRepository.getAuthToken()
      
      if (token == null) {
        Log.e("CVReviewVM", "No auth token found")
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Session expired. Please login again."
        )
        return
      }
      
      Log.d("CVReviewVM", "Starting online CV upload...")
      
      cvReviewRepository.uploadCV(token, cvFile, careerField)
        .onSuccess { response ->
          Log.d("CVReviewVM", "Upload successful: ${response.message}")
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            cvReviewData = response.data,
            isUploadSuccess = true,
            isOffline = false
          )
        }
        .onFailure { exception ->
          Log.e("CVReviewVM", "Upload failed", exception)
          
          // Kalau network error, save ke offline queue
          if (exception.message?.contains("network", true) == true || 
              exception.message?.contains("connection", true) == true) {
            Log.d("CVReviewVM", "Network error detected, saving to offline queue...")
            saveToOfflineQueue(cvFile, careerField)
          } else {
            _uiState.value = _uiState.value.copy(
              isLoading = false,
              error = exception.message ?: "Upload gagal, coba lagi ya!"
            )
          }
        }
    } catch (e: Exception) {
      Log.e("CVReviewVM", "Error getting token", e)
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        error = "Failed to get authentication token"
      )
    }
  }
  
  private suspend fun saveToOfflineQueue(cvFile: File, careerField: String) {
    try {
      Log.d("CVReviewVM", "Saving CV to offline queue...")
      
      // Generate temporary ID
      val tempId = UUID.randomUUID().toString()
      
      // Create entity untuk offline upload
      val offlineEntity = CVReviewEntity(
        id = tempId,
        fileName = cvFile.name,
        careerField = careerField,
        uploadDate = System.currentTimeMillis(),
        overallScore = 0f, // Will be filled after upload
        technicalScore = 0f,
        softSkillScore = 0f,
        experienceScore = 0f,
        aiAnalysis = "Pending upload...",
        suggestions = "Will be available after upload",
        status = "pending",
        filePath = cvFile.absolutePath,
        isOfflineUpload = true,
        syncStatus = "pending_upload"
      )
      
      // Save ke Room database
      cvReviewRepository.saveCVReviewLocal(offlineEntity)
      
      Log.d("CVReviewVM", "CV saved to offline queue successfully")
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        isUploadSuccess = true,
        isOffline = true,
        offlineMessage = "CV disimpan offline. Akan diupload otomatis saat online."
      )
      
    } catch (e: Exception) {
      Log.e("CVReviewVM", "Failed to save offline", e)
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        error = "Gagal menyimpan CV offline: ${e.message}"
      )
    }
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }
  
  fun clearOfflineMessage() {
    _uiState.value = _uiState.value.copy(offlineMessage = null)
  }

  fun resetUploadState() {
    _uiState.value = CVReviewUiState()
  }
}

data class CVReviewUiState(
  val isLoading: Boolean = false,
  val cvReviewData: CVReviewData? = null,
  val isUploadSuccess: Boolean = false,
  val error: String? = null,
  val isOffline: Boolean = false,
  val offlineMessage: String? = null
) 