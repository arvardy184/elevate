package com.application.elevate.data.repository

import com.application.elevate.model.AssessmentHistory
import com.application.elevate.model.AssessmentHistoryResponse
import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.UserResponse
import kotlinx.coroutines.flow.Flow

interface AssessmentOfflineRepository {
    // Online methods
    suspend fun submitAssessment(request: AssessmentRequest): Flow<Result<UserResponse>>
    suspend fun getAssessmentHistory(): Flow<Result<AssessmentHistoryResponse>>
    
    // Offline-first methods
    suspend fun submitAssessmentOfflineFirst(request: AssessmentRequest): Flow<Result<AssessmentHistory>>
    suspend fun getAssessmentHistoryOfflineFirst(userId: Int): Flow<Result<List<AssessmentHistory>>>
    suspend fun getLatestAssessmentOfflineFirst(userId: Int): Flow<Result<AssessmentHistory?>>
    
    // Local storage methods
    suspend fun getAssessmentHistoryLocal(userId: Int): List<AssessmentHistory>
    suspend fun getLatestAssessmentLocal(userId: Int): AssessmentHistory?
    suspend fun saveAssessmentLocal(request: AssessmentRequest, userId: Int, isSynced: Boolean = true)
    suspend fun saveAssessmentHistoryLocal(assessments: List<AssessmentHistory>, userId: Int, isSynced: Boolean = true)
    
    // Sync methods
    suspend fun syncAssessments(): Flow<Result<Boolean>>
    suspend fun syncUnsyncedAssessments(): Flow<Result<Int>>
    
    // Utility methods
    suspend fun hasUserAssessment(userId: Int): Boolean
} 