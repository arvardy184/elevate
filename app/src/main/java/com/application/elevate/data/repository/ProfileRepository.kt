package com.application.elevate.data.repository

import com.application.elevate.model.ProfileResponse
import com.application.elevate.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    // Online methods
    suspend fun getProfile(): Flow<Result<ProfileResponse>>
    suspend fun updateProfile(user: User): Flow<Result<ProfileResponse>>
    
    // Offline-first methods
    suspend fun getProfileOfflineFirst(userId: Int): Flow<Result<User>>
    suspend fun updateProfileOfflineFirst(user: User): Flow<Result<User>>
    
    // Local storage methods
    suspend fun getProfileLocal(userId: Int): User?
    suspend fun saveProfileLocal(user: User, isSynced: Boolean = true)
    
    // Sync methods
    suspend fun syncProfile(): Flow<Result<Boolean>>
    suspend fun syncUnsyncedProfiles(): Flow<Result<Int>>
}