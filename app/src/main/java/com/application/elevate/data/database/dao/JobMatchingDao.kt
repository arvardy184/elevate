package com.application.elevate.data.database.dao

import androidx.room.*
import com.application.elevate.data.database.entity.JobMatchingEntity
import com.application.elevate.data.database.entity.JobMatchEntity
import com.application.elevate.data.database.entity.AIAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobMatchingDao {
    
    // Job Matching Entity
    @Query("SELECT * FROM job_matching WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllJobMatching(userId: Int): Flow<List<JobMatchingEntity>>
    
    @Query("SELECT * FROM job_matching ORDER BY createdAt DESC")
    fun getAllJobMatchingForAllUsers(): Flow<List<JobMatchingEntity>>
    
    @Query("SELECT * FROM job_matching WHERE id = :id")
    suspend fun getJobMatchingById(id: String): JobMatchingEntity?
    
    @Query("SELECT * FROM job_matching WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedJobMatching(userId: Int): List<JobMatchingEntity>
    
    @Query("SELECT * FROM job_matching WHERE isSynced = 0")
    suspend fun getAllUnsyncedJobMatching(): List<JobMatchingEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobMatching(jobMatching: JobMatchingEntity)
    
    @Update
    suspend fun updateJobMatching(jobMatching: JobMatchingEntity)
    
    @Query("UPDATE job_matching SET isSynced = 1, syncError = null WHERE id = :id")
    suspend fun markAsSynced(id: String)
    
    @Query("UPDATE job_matching SET syncError = :error WHERE id = :id")
    suspend fun updateSyncError(id: String, error: String)
    
    @Delete
    suspend fun deleteJobMatching(jobMatching: JobMatchingEntity)
    
    @Query("DELETE FROM job_matching")
    suspend fun deleteAllJobMatching()
    
    // Job Match Entity  
    @Query("SELECT * FROM job_match WHERE jobMatchingId = :jobMatchingId")
    suspend fun getJobMatchesByJobMatchingId(jobMatchingId: String): List<JobMatchEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobMatches(jobMatches: List<JobMatchEntity>)
    
    @Query("DELETE FROM job_match WHERE jobMatchingId = :jobMatchingId")
    suspend fun deleteJobMatchesByJobMatchingId(jobMatchingId: String)
    
    // AI Analysis Entity
    @Query("SELECT * FROM ai_analysis WHERE jobMatchingId = :jobMatchingId")
    suspend fun getAIAnalysisByJobMatchingId(jobMatchingId: String): AIAnalysisEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIAnalysis(aiAnalysis: AIAnalysisEntity)
    
    @Query("DELETE FROM ai_analysis WHERE jobMatchingId = :jobMatchingId")
    suspend fun deleteAIAnalysisByJobMatchingId(jobMatchingId: String)
    
    // Complete operations with transaction
    @Transaction
    suspend fun insertCompleteJobMatching(
        jobMatching: JobMatchingEntity,
        jobMatches: List<JobMatchEntity>,
        aiAnalysis: AIAnalysisEntity
    ) {
        insertJobMatching(jobMatching)
        insertJobMatches(jobMatches)
        insertAIAnalysis(aiAnalysis)
    }
    
    @Transaction
    suspend fun deleteCompleteJobMatching(jobMatchingId: String) {
        deleteJobMatchesByJobMatchingId(jobMatchingId)
        deleteAIAnalysisByJobMatchingId(jobMatchingId)
        getJobMatchingById(jobMatchingId)?.let { deleteJobMatching(it) }
    }
} 