package com.application.elevate.data.api

import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AssessmentApiService {
    @POST("assessment")
    suspend fun submitAssessment(
        @Header("Authorization") token: String,
        @Body request: AssessmentRequest
    ): UserResponse
} 