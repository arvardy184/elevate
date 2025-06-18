package com.application.elevate.api

import com.application.elevate.model.CVReviewDetailResponse
import com.application.elevate.model.CVReviewListResponse
import com.application.elevate.model.CVReviewResponse
import com.application.elevate.model.jobmatching.JobMatchingResponse
import com.application.elevate.model.jobmatching.JobMatchingHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface JobMatchingApiService {
    
    @Multipart
    @POST("job-matching/upload-and-match")
    suspend fun uploadAndMatchJobs(
        @Header("Authorization") authorization: String,
        @Part("dreamJob") dreamJob: RequestBody,
        @Part cv: MultipartBody.Part
    ): Response<JobMatchingResponse>
    
    @GET("job-matching/history")
    suspend fun getJobMatchingHistory(
        @Header("Authorization") authorization: String
    ): Response<JobMatchingHistoryResponse>
}


