package com.application.elevate.data.repository.jobmatching

import com.application.elevate.model.jobmatching.JobMatchingResponse
import java.io.File

sealed class JobMatchingResult {
    data class Success(val data: JobMatchingResponse) : JobMatchingResult()
    data class Error(val message: String) : JobMatchingResult()
    object Loading : JobMatchingResult()
}

interface JobMatchingRepository {
    suspend fun uploadAndMatchJobs(
        cvFile: File,
        dreamJob: String
    ): JobMatchingResult
} 