package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.AssessmentApiService
import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.AssessmentHistoryResponse
import com.application.elevate.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssessmentRepository @Inject constructor(
    private val assessmentApiService: AssessmentApiService
) : AssessmentRepositoryInterface {
    private val TAG = "AssessmentRepository"

    override suspend fun submitAssessment(token: String, request: AssessmentRequest): Flow<Result<UserResponse>> = flow {
        try {
            Log.d(TAG, "Mengirim assessment dengan token")
            // Format token yang benar
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Log.d(TAG, "Request body: $request")
            val response = assessmentApiService.submitAssessment(formattedToken, request)
            Log.d(TAG, "Assessment berhasil dikirim: $response")
            if (response.user != null) {
                Log.d(TAG, "User data after assessment: ${response.user}")
                Log.d(TAG, "Assessment status: ${response.user.isAssessmentCompleted}")
            } else {
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
                Log.e(TAG, "Error response body: ${e.response()?.errorBody()?.string()}")
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

    override suspend fun getAssessmentHistory(token: String): Flow<Result<AssessmentHistoryResponse>> = flow {
        try {
            Log.d(TAG, "Mengambil riwayat assessment")
            // Format token yang benar
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = assessmentApiService.getAssessmentHistory(formattedToken)
            Log.d(TAG, "Riwayat assessment berhasil diambil: $response")
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting assessment history: ${e.message}")
            throw e
        }
    }.catch { e ->
        when (e) {
            is HttpException -> {
                Log.e(TAG, "HTTP error during get history: ${e.code()}, message: ${e.message()}")
                emit(Result.failure(Exception("Server error: ${e.code()} - ${e.message()}")))
            }
            is IOException -> {
                Log.e(TAG, "Network error during get history: ${e.message}")
                emit(Result.failure(Exception("Network error: Periksa koneksi internet Anda")))
            }
            else -> {
                Log.e(TAG, "Unexpected error during get history: ${e.message}")
                emit(Result.failure(Exception("Error: ${e.message ?: "Unknown error"}")))
            }
        }
    }
} 