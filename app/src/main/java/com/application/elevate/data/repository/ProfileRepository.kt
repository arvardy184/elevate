package com.application.elevate.data.repository

import com.application.elevate.model.UserResponse
import kotlinx.coroutines.flow.Flow


import com.application.elevate.model.ProfileResponse
import com.application.elevate.model.User

interface ProfileRepository {
    suspend fun getProfile(): Flow<Result<ProfileResponse>>
    suspend fun updateProfile(user: User): Flow<Result<ProfileResponse>>
}