package com.application.elevate.data.repository

import com.application.elevate.model.UserResponse
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<Result<UserResponse>>
} 