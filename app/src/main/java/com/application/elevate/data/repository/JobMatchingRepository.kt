package com.application.elevate.data.repository

import com.application.elevate.model.JobMatchingResponse
import com.application.elevate.model.JobMatchingHistoryResponse
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
    
    suspend fun syncPendingData(): List<SyncResult>
    suspend fun getUnsyncedCount(): Int
} 