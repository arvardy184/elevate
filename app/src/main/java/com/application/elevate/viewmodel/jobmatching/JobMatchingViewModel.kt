package com.application.elevate.viewmodel.jobmatching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.jobmatching.JobMatchingRepository
import com.application.elevate.data.repository.jobmatching.JobMatchingResult
import com.application.elevate.data.repository.jobmatching.JobMatchingHistoryResult
import com.application.elevate.model.jobmatching.JobMatchingResponse
import com.application.elevate.model.jobmatching.JobMatchingHistoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

// Enhanced error types for better error handling
sealed class JobMatchingError {
    object NetworkError : JobMatchingError()
    object AuthenticationError : JobMatchingError()
    object FileValidationError : JobMatchingError()
    object ServerError : JobMatchingError()
    object UnknownError : JobMatchingError()
    data class CustomError(val message: String) : JobMatchingError()
}

data class JobMatchingUiState(
    val isLoading: Boolean = false,
    val jobMatchingResult: JobMatchingResponse? = null,
    val historyResult: JobMatchingHistoryResponse? = null,
    val errorMessage: String? = null,
    val errorType: JobMatchingError? = null,
    val isUploadSuccessful: Boolean = false,
    val canRetry: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(),
    val isLoadingHistory: Boolean = false
)

@HiltViewModel
class JobMatchingViewModel @Inject constructor(
    private val jobMatchingRepository: JobMatchingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobMatchingUiState())
    val uiState: StateFlow<JobMatchingUiState> = _uiState.asStateFlow()

    fun uploadAndMatchJobs(cvFile: File, dreamJob: String) {
        // Input validation first
        val validationErrors = validateInputs(cvFile, dreamJob)
        if (validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                validationErrors = validationErrors,
                errorMessage = "Please fix the validation errors",
                errorType = JobMatchingError.FileValidationError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                errorType = null,
                validationErrors = emptyMap(),
                canRetry = false
            )

            when (val result = jobMatchingRepository.uploadAndMatchJobs(cvFile, dreamJob)) {
                is JobMatchingResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobMatchingResult = result.data,
                        isUploadSuccessful = true,
                        errorMessage = null,
                        errorType = null,
                        canRetry = false
                    )
                }
                is JobMatchingResult.Error -> {
                    val (errorType, canRetry) = categorizeError(result.message)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(errorType, result.message),
                        errorType = errorType,
                        isUploadSuccessful = false,
                        canRetry = canRetry
                    )
                }
                is JobMatchingResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun retryUpload() {
        val currentState = _uiState.value
        if (currentState.canRetry) {
            // Note: We need to store the last file and dreamJob for retry
            // For now, we'll just reset the error state
            _uiState.value = currentState.copy(
                errorMessage = null,
                errorType = null,
                canRetry = false
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            errorType = null,
            validationErrors = emptyMap(),
            canRetry = false
        )
    }

    fun resetState() {
        _uiState.value = JobMatchingUiState()
    }

    private fun validateInputs(cvFile: File, dreamJob: String): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Validate dream job
        if (dreamJob.isBlank()) {
            errors["dreamJob"] = "Dream job position is required"
        } else if (dreamJob.length < 3) {
            errors["dreamJob"] = "Dream job position must be at least 3 characters"
        }

        // Validate CV file
        if (!cvFile.exists()) {
            errors["cvFile"] = "CV file not found"
        } else {
            // Check file size (max 10MB)
            val maxSize = 10 * 1024 * 1024 // 10MB in bytes
            if (cvFile.length() > maxSize) {
                errors["cvFile"] = "File size too large. Maximum 10MB allowed"
            }

            // Check file extension
            if (!cvFile.name.lowercase().endsWith(".pdf")) {
                errors["cvFile"] = "Only PDF files are supported"
            }

            // Check if file is readable
            if (!cvFile.canRead()) {
                errors["cvFile"] = "Cannot read the selected file"
            }
        }

        return errors
    }

    private fun categorizeError(errorMessage: String): Pair<JobMatchingError, Boolean> {
        return when {
            errorMessage.contains("network", ignoreCase = true) ||
            errorMessage.contains("connection", ignoreCase = true) ||
            errorMessage.contains("timeout", ignoreCase = true) -> {
                JobMatchingError.NetworkError to true
            }
            errorMessage.contains("token", ignoreCase = true) ||
            errorMessage.contains("unauthorized", ignoreCase = true) ||
            errorMessage.contains("authentication", ignoreCase = true) -> {
                JobMatchingError.AuthenticationError to false
            }
            errorMessage.contains("500") ||
            errorMessage.contains("server", ignoreCase = true) -> {
                JobMatchingError.ServerError to true
            }
            errorMessage.contains("file", ignoreCase = true) ||
            errorMessage.contains("format", ignoreCase = true) -> {
                JobMatchingError.FileValidationError to false
            }
            else -> JobMatchingError.CustomError(errorMessage) to true
        }
    }

    fun getJobMatchingHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingHistory = true,
                errorMessage = null,
                errorType = null
            )

            when (val result = jobMatchingRepository.getJobMatchingHistory()) {
                is JobMatchingHistoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingHistory = false,
                        historyResult = result.data,
                        errorMessage = null,
                        errorType = null
                    )
                }
                is JobMatchingHistoryResult.Error -> {
                    val (errorType, canRetry) = categorizeError(result.message)
                    _uiState.value = _uiState.value.copy(
                        isLoadingHistory = false,
                        errorMessage = getErrorMessage(errorType, result.message),
                        errorType = errorType,
                        canRetry = canRetry
                    )
                }
                is JobMatchingHistoryResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoadingHistory = true)
                }
            }
        }
    }

    private fun getErrorMessage(errorType: JobMatchingError, originalMessage: String): String {
        return when (errorType) {
            JobMatchingError.NetworkError -> 
                "Koneksi bermasalah nih! Coba cek internet kamu dan ulangi lagi."
            JobMatchingError.AuthenticationError -> 
                "Sesi kamu udah expired. Login ulang ya!"
            JobMatchingError.FileValidationError -> 
                "File CV-nya ada masalah. Pastikan formatnya PDF dan ukuran max 10MB."
            JobMatchingError.ServerError -> 
                "Server lagi down nih. Coba lagi dalam beberapa menit ya!"
            JobMatchingError.UnknownError -> 
                "Ada error yang gak terduga. Coba lagi atau hubungi support."
            is JobMatchingError.CustomError -> 
                errorType.message
        }
    }
} 