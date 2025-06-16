package com.application.elevate.data.repository

import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.AssessmentHistoryResponse
import com.application.elevate.model.UserResponse
import kotlinx.coroutines.flow.Flow

interface AssessmentRepositoryInterface {
    suspend fun submitAssessment(token: String, request: AssessmentRequest): Flow<Result<UserResponse>>
    suspend fun getAssessmentHistory(token: String): Flow<Result<AssessmentHistoryResponse>>
} 