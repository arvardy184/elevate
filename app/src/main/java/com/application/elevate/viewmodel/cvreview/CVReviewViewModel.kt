package com.application.elevate.viewmodel.cvreview

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.CVReviewRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.CVReviewData
import com.application.elevate.util.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CVReviewViewModel @Inject constructor(
    private val cvReviewRepository: CVReviewRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CVReviewUiState())
    val uiState: StateFlow<CVReviewUiState> = _uiState.asStateFlow()

    fun uploadCV(fileUri: Uri, careerField: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploading = true,
                isLoading = true,
                error = null,
                uploadProgress = 0f
            )

            try {
                // Get user token
                val token = userRepository.getAuthToken()
                Log.d("CVReviewVM", "Raw token from userRepository: ${token?.take(50)}...")
                
                if (token.isNullOrEmpty()) {
                    Log.e("CVReviewVM", "Token is null or empty!")
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        isLoading = false,
                        error = "Authentication required. Please login again."
                    )
                    return@launch
                }

                // Format token for authorization header
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                Log.d("CVReviewVM", "Authorization header: ${authHeader.take(50)}...")

                // Convert URI to File
                val file = FileUtil.createFileFromUri(context, fileUri, "cv.pdf")
                if (file == null) {
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        isLoading = false,
                        error = "Failed to process selected file"
                    )
                    return@launch
                }

                // Create multipart request
                val requestFile = file.asRequestBody("application/pdf".toMediaType())
                val filePart = MultipartBody.Part.createFormData("cv", "cv.pdf", requestFile)
                val careerFieldBody = careerField.toRequestBody("text/plain".toMediaType())

                Log.d("CVReviewVM", "File size: ${file.length()} bytes")
                Log.d("CVReviewVM", "Career field: $careerField")

                // Simulate upload progress
                _uiState.value = _uiState.value.copy(uploadProgress = 0.3f)

                // Upload CV
                Log.d("CVReviewVM", "Starting CV upload to API...")
                val result = cvReviewRepository.uploadCV(authHeader, filePart, careerFieldBody)
                
                _uiState.value = _uiState.value.copy(uploadProgress = 0.8f)

                result.fold(
                    onSuccess = { response ->
                        Log.d("CVReviewVM", "CV uploaded successfully: ${response.message}")
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            isLoading = false,
                            uploadProgress = 1f,
                            cvReviewData = response.data,
                            uploadSuccess = true,
                            isUploadSuccess = true
                        )
                    },
                    onFailure = { exception ->
                        Log.e("CVReviewVM", "Failed to upload CV", exception)
                        Log.e("CVReviewVM", "Exception type: ${exception::class.java.simpleName}")
                        Log.e("CVReviewVM", "Exception message: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            isLoading = false,
                            uploadProgress = 0f,
                            error = "Upload failed: ${exception.message}"
                        )
                    }
                )

                // Clean up temporary file
                file.delete()

            } catch (e: Exception) {
                Log.e("CVReviewVM", "Error during CV upload", e)
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    isLoading = false,
                    uploadProgress = 0f,
                    error = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    // Overloaded method for File parameter
    fun uploadCV(file: File, careerField: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploading = true,
                isLoading = true,
                error = null,
                uploadProgress = 0f
            )

            try {
                // Get user token
                val token = userRepository.getAuthToken()
                Log.d("CVReviewVM", "Raw token from userRepository: ${token?.take(50)}...")
                
                if (token.isNullOrEmpty()) {
                    Log.e("CVReviewVM", "Token is null or empty!")
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        isLoading = false,
                        error = "Authentication required. Please login again."
                    )
                    return@launch
                }

                // Format token for authorization header
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                Log.d("CVReviewVM", "Authorization header: ${authHeader.take(50)}...")

                // Create multipart request
                val requestFile = file.asRequestBody("application/pdf".toMediaType())
                val filePart = MultipartBody.Part.createFormData("cv", "cv.pdf", requestFile)
                val careerFieldBody = careerField.toRequestBody("text/plain".toMediaType())

                Log.d("CVReviewVM", "File size: ${file.length()} bytes")
                Log.d("CVReviewVM", "Career field: $careerField")

                // Simulate upload progress
                _uiState.value = _uiState.value.copy(uploadProgress = 0.3f)

                // Upload CV
                Log.d("CVReviewVM", "Starting CV upload to API...")
                val result = cvReviewRepository.uploadCV(authHeader, filePart, careerFieldBody)
                
                _uiState.value = _uiState.value.copy(uploadProgress = 0.8f)

                result.fold(
                    onSuccess = { response ->
                        Log.d("CVReviewVM", "CV uploaded successfully: ${response.message}")
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            isLoading = false,
                            uploadProgress = 1f,
                            cvReviewData = response.data,
                            uploadSuccess = true,
                            isUploadSuccess = true
                        )
                    },
                    onFailure = { exception ->
                        Log.e("CVReviewVM", "Failed to upload CV", exception)
                        Log.e("CVReviewVM", "Exception type: ${exception::class.java.simpleName}")
                        Log.e("CVReviewVM", "Exception message: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            isLoading = false,
                            uploadProgress = 0f,
                            error = "Upload failed: ${exception.message}"
                        )
                    }
                )

            } catch (e: Exception) {
                Log.e("CVReviewVM", "Error during CV upload", e)
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    isLoading = false,
                    uploadProgress = 0f,
                    error = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearUploadSuccess() {
        _uiState.value = _uiState.value.copy(uploadSuccess = false)
    }

    fun resetUploadState() {
        _uiState.value = CVReviewUiState()
    }
}

data class CVReviewUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val cvReviewData: CVReviewData? = null,
    val error: String? = null,
    val uploadSuccess: Boolean = false,
    val isUploadSuccess: Boolean = false
)
