package com.application.elevate.data.repository

import com.application.elevate.data.api.AssessmentApiService
import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.UserResponse
import javax.inject.Inject

class AssessmentRepository @Inject constructor(
    private val assessmentApiService: AssessmentApiService
) {
    suspend fun submitAssessment(token: String, request: AssessmentRequest): Result<UserResponse> {
        return try {
            val response = assessmentApiService.submitAssessment("Bearer $token", request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 