package com.application.elevate.data.api


import com.application.elevate.model.RegisterRequest
import com.application.elevate.model.UserRequest
import com.application.elevate.model.UserResponse
import com.application.elevate.model.AssessmentRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @POST("auth/login")
    suspend fun login(@Body request: UserRequest): UserResponse

    @POST("assessment")
    suspend fun submitAssessment(
        @Header("Authorization") token: String,
        @Body request: AssessmentRequest
    ): UserResponse
}