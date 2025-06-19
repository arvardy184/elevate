package com.application.elevate.data.repository

import com.application.elevate.data.database.entity.JobMatchingEntity
import com.application.elevate.model.JobMatchingResponse
import com.application.elevate.model.JobMatchingHistoryResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface JobMatchingOfflineRepository {
    
    // Local data operations
    fun getAllJobMatchingLocal(): Flow<List<JobMatchingEntity>>
    suspend fun getJobMatchingByIdLocal(id: String): JobMatchingEntity?
    suspend fun saveJobMatchingLocal(jobMatchingResponse: JobMatchingResponse, isOfflineData: Boolean = false)
    suspend fun saveOfflineJobMatching(cvFile: File, dreamJob: String): String // Returns generated ID
    
    // Sync operations
    suspend fun getUnsyncedJobMatching(): List<JobMatchingEntity>
    suspend fun markAsSynced(id: String)
    suspend fun updateSyncError(id: String, error: String)
    suspend fun syncPendingData(): List<SyncResult>
    
    // Utility
    suspend fun deleteJobMatching(id: String)
    suspend fun clearAllData()
}

sealed class SyncResult {
    data class Success(val id: String) : SyncResult()
    data class Error(val id: String, val error: String) : SyncResult()
} 