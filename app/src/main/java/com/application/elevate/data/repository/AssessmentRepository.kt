package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.AssessmentApiService
import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.AssessmentHistoryResponse
import com.application.elevate.model.UserResponse
import javax.inject.Inject

class AssessmentRepository @Inject constructor(
    private val assessmentApiService: AssessmentApiService
) {
    private val TAG = "AssessmentRepository"

    suspend fun submitAssessment(token: String, request: AssessmentRequest): Result<UserResponse> {
        return try {
            Log.d(TAG, "Mengirim assessment dengan token")
            // Format token yang benar
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = assessmentApiService.submitAssessment(formattedToken, request)
            Log.d(TAG, "Assessment berhasil dikirim")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengirim assessment: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAssessmentHistory(token: String): Result<AssessmentHistoryResponse> {
        return try {
            Log.d(TAG, "Mengambil riwayat assessment")
            // Format token yang benar
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = assessmentApiService.getAssessmentHistory(formattedToken)
            Log.d(TAG, "Riwayat assessment berhasil diambil")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil riwayat assessment: ${e.message}")
            Result.failure(e)
        }
    }
} 