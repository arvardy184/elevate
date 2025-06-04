package com.application.elevate.data.repository

import com.application.elevate.model.RegisterRequest
import com.application.elevate.model.UserRequest
import com.application.elevate.model.UserResponse
import com.application.elevate.model.AssessmentRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: UserRequest): Flow<Result<UserResponse>>
    suspend fun register(request: RegisterRequest): Flow<Result<UserResponse>>
    suspend fun submitAssessment(token: String, request: AssessmentRequest): Flow<Result<UserResponse>>
} 