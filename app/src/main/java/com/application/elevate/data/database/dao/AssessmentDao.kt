package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.AssessmentEntity

@Dao
interface AssessmentDao {
    
    @Query("SELECT * FROM assessment WHERE userId = :userId ORDER BY lastModified DESC LIMIT 1")
    suspend fun getLatestAssessment(userId: Int): AssessmentEntity?
    
    @Query("SELECT * FROM assessment WHERE userId = :userId ORDER BY lastModified DESC LIMIT 1")
    fun getLatestAssessmentFlow(userId: Int): Flow<AssessmentEntity?>
    
    @Query("SELECT * FROM assessment WHERE userId = :userId ORDER BY lastModified DESC")
    suspend fun getAllAssessments(userId: Int): List<AssessmentEntity>
    
    @Query("SELECT * FROM assessment WHERE userId = :userId ORDER BY lastModified DESC")
    fun getAllAssessmentsFlow(userId: Int): Flow<List<AssessmentEntity>>
    
    @Query("SELECT * FROM assessment WHERE id = :assessmentId")
    suspend fun getAssessmentById(assessmentId: Int): AssessmentEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAssessments(assessments: List<AssessmentEntity>)
    
    @Update
    suspend fun updateAssessment(assessment: AssessmentEntity)
    
    @Query("DELETE FROM assessment WHERE id = :assessmentId")
    suspend fun deleteAssessment(assessmentId: Int)
    
    @Query("DELETE FROM assessment WHERE userId = :userId")
    suspend fun deleteAllUserAssessments(userId: Int)
    
    // Untuk sync operations
    @Query("SELECT * FROM assessment WHERE isSynced = 0")
    suspend fun getUnsyncedAssessments(): List<AssessmentEntity>
    
    @Query("SELECT * FROM assessment WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedUserAssessments(userId: Int): List<AssessmentEntity>
    
    @Query("UPDATE assessment SET isSynced = 1, syncError = NULL WHERE id = :assessmentId")
    suspend fun markAsSynced(assessmentId: Int)
    
    @Query("UPDATE assessment SET isSynced = 0, syncError = :error WHERE id = :assessmentId")
    suspend fun markAsUnsynced(assessmentId: Int, error: String?)
    
    @Query("SELECT COUNT(*) FROM assessment WHERE userId = :userId")
    suspend fun getAssessmentCount(userId: Int): Int
    
    @Query("DELETE FROM assessment")
    suspend fun deleteAllAssessments()
    
    // Check if user has assessment
    @Query("SELECT COUNT(*) > 0 FROM assessment WHERE userId = :userId")
    suspend fun hasAssessment(userId: Int): Boolean
} 