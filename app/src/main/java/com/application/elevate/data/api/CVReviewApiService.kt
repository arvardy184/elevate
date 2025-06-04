package com.application.elevate.api

import com.application.elevate.model.CVReviewResponse
import com.application.elevate.model.CVReviewListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface CVReviewApiService {
  @Multipart
  @POST("cv-review/upload")
  suspend fun uploadCV(
    @Header("Authorization") token: String,
    @Part cv: MultipartBody.Part,
    @Part("careerField") careerField: RequestBody
  ): CVReviewResponse

  @GET("cv-review/my-reviews")
  suspend fun getMyCVReviews(
    @Header("Authorization") token: String,
    @Query("page") page: Int = 1,
    @Query("limit") limit: Int = 10
  ): CVReviewListResponse

  @GET("cv-review/{id}")
  suspend fun getCVReviewById(
    @Header("Authorization") token: String,
    @Path("id") id: String
  ): CVReviewResponse

  @FormUrlEncoded
  @PUT("cv-review/{id}")
  suspend fun updateCVReview(
    @Header("Authorization") token: String,
    @Path("id") id: String,
    @Field("careerField") careerField: String
  ): CVReviewResponse

  @DELETE("cv-review/{id}")
  suspend fun deleteCVReview(
    @Header("Authorization") token: String,
    @Path("id") id: String
  ): CVReviewResponse
} 

