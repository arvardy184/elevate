package com.application.elevate.data.repository

import android.content.Context
import com.application.elevate.data.api.AuthApiService
import com.application.elevate.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val userRepository: UserRepository,
    private val context: Context
) : ProfileRepository {
    
    override suspend fun getProfile(): Flow<Result<UserResponse>> = flow {
        try {
            // Ambil token dari userRepository
            val token = userRepository.getAuthToken()
            if (token != null) {
                // Dummy response untuk sementara - bisa diganti dengan API call
                val user = userRepository.getUser()
                if (user != null) {
                    val response = UserResponse(
//                        success = true,
                        message = "Profile loaded successfully",
                        user = user,
                        token = token
                    )
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(Exception("User not found")))
                }
            } else {
                emit(Result.failure(Exception("Token not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 