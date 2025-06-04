package com.application.elevate.data.api

import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.AssessmentHistoryResponse
import com.application.elevate.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AssessmentApiService {
    //mengirim data
    @POST("assessment")
    suspend fun submitAssessment(
        @Header("Authorization") token: String,
        @Body request: AssessmentRequest
    ): UserResponse

    //ambil data dari API untuk menampilkan data
    @GET("assessment/history")
    suspend fun getAssessmentHistory(
        @Header("Authorization") token: String
    ): AssessmentHistoryResponse
} 