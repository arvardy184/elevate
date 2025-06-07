package com.application.elevate.data.repository

import com.application.elevate.model.ProfileResponse
import com.application.elevate.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<Result<ProfileResponse>>
    suspend fun updateProfile(user: User): Flow<Result<ProfileResponse>>
} 