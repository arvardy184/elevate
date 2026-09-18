package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.AuthApiService
import com.application.elevate.model.RegisterRequest
import com.application.elevate.model.UserRequest
import com.application.elevate.model.UserResponse
import com.application.elevate.model.AssessmentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService
) : AuthRepository {
    private val TAG = "AuthRepository"
    
    override suspend fun login(request: UserRequest): Flow<Result<UserResponse>> = flow {
        try {
            val response = api.login(request)
            if (response.user == null) {
                Log.e(TAG, "User data is null in response")
            }
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error during login: ${e.message}")
            throw e
        }
    }.catch { e ->
        when (e) {
            is HttpException -> {
                Log.e(TAG, "HTTP error during login: ${e.code()}, message: ${e.message()}")
                emit(Result.failure(Exception("Server error: ${e.code()} - ${e.message()}")))
            }
            is IOException -> {
                Log.e(TAG, "Network error during login: ${e.message}")
                emit(Result.failure(Exception("Network error: Periksa koneksi internet Anda")))
            }
            else -> {
                Log.e(TAG, "Unexpected error during login: ${e.message}")
                emit(Result.failure(Exception("Error: ${e.message ?: "Unknown error"}")))
            }
        }
    }

    override suspend fun register(request: RegisterRequest): Flow<Result<UserResponse>> = flow {
        try {
            val response = api.register(request)
            if (response.user == null) {
                Log.e(TAG, "User data is null in response")
            }
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error during register: ${e.message}")
            throw e
        }
    }.catch { e ->
        when (e) {
            is HttpException -> {
                Log.e(TAG, "HTTP error during register: ${e.code()}, message: ${e.message()}")
                emit(Result.failure(Exception("Server error: ${e.code()} - ${e.message()}")))
            }
            is IOException -> {
                Log.e(TAG, "Network error during register: ${e.message}")
                emit(Result.failure(Exception("Network error: Periksa koneksi internet Anda")))
            }
            else -> {
                Log.e(TAG, "Unexpected error during register: ${e.message}")
                emit(Result.failure(Exception("Error: ${e.message ?: "Unknown error"}")))
            }
        }
    }

    override suspend fun submitAssessment(token: String, request: AssessmentRequest): Flow<Result<UserResponse>> = flow {
        try {
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.submitAssessment(authToken, request)
            if (response.user == null) {
                Log.e(TAG, "User data is null in assessment response")
            }
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error during assessment: ${e.message}")
            throw e
        }
    }.catch { e ->
        when (e) {
            is HttpException -> {
                Log.e(TAG, "HTTP error during assessment: ${e.code()}, message: ${e.message()}")
                emit(Result.failure(Exception("Server error: ${e.code()} - ${e.message()}")))
            }
            is IOException -> {
                Log.e(TAG, "Network error during assessment: ${e.message}")
                emit(Result.failure(Exception("Network error: Periksa koneksi internet Anda")))
            }
            else -> {
                Log.e(TAG, "Unexpected error during assessment: ${e.message}")
                emit(Result.failure(Exception("Error: ${e.message ?: "Unknown error"}")))
            }
        }
    }
}
