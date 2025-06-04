package com.application.elevate.data.api

import com.application.elevate.model.ConsultantResponse
import com.application.elevate.model.ConsultantDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CounselingApiService {

  @GET("counseling/counselors")
  suspend fun getCounselors(
    @Query("page") page: Int = 1,
    @Query("limit") limit: Int = 10,
    @Query("specialization") specialization: String? = null
  ): ConsultantResponse
  
  @GET("counseling/counselors/{id}")
  suspend fun getCounselorDetail(
    @Path("id") counselorId: Int
  ): ConsultantDetailResponse
} 