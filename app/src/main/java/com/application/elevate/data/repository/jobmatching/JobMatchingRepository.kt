package com.application.elevate.data.repository.jobmatching

import com.application.elevate.model.jobmatching.JobMatchingResponse
import com.application.elevate.model.jobmatching.JobMatchingHistoryResponse
import java.io.File

sealed class JobMatchingResult {
    data class Success(val data: JobMatchingResponse) : JobMatchingResult()
    data class Error(val message: String) : JobMatchingResult()
    object Loading : JobMatchingResult()
}

sealed class JobMatchingHistoryResult {
    data class Success(val data: JobMatchingHistoryResponse) : JobMatchingHistoryResult()
    data class Error(val message: String) : JobMatchingHistoryResult()
    object Loading : JobMatchingHistoryResult()
}

interface JobMatchingRepository {
    suspend fun uploadAndMatchJobs(
        cvFile: File,
        dreamJob: String
    ): JobMatchingResult
    
    suspend fun getJobMatchingHistory(): JobMatchingHistoryResult
} 