package com.application.elevate.data.repository

import com.application.elevate.model.CVReviewResponse
import com.application.elevate.model.CVReviewListResponse
import com.application.elevate.data.database.entity.CVReviewEntity
import com.application.elevate.model.CVReviewDetailResponse
import kotlinx.coroutines.flow.Flow
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CVReviewRepository {
  suspend fun uploadCV(
    token: String,
    cvFile: MultipartBody.Part,
    careerField: RequestBody
  ): Result<CVReviewResponse>

  suspend fun getMyCVReviews(
    token: String,
    page: Int = 1,
    limit: Int = 10
  ): Result<CVReviewListResponse>

  suspend fun getCVReviewById(
    token: String,
    reviewId: String
  ): Result<CVReviewDetailResponse>

  suspend fun updateCVReview(
    token: String,
    reviewId: String,
    careerField: String
  ): Result<CVReviewResponse>

  suspend fun deleteCVReview(
    token: String,
    id: String
  ): Result<CVReviewResponse>
  
  // Offline support methods
  fun getAllCVReviewsLocal(): Flow<List<CVReviewEntity>>
  suspend fun getCVReviewByIdLocal(
    reviewId: String
  ): CVReviewEntity?
  suspend fun saveCVReviewLocal(
    entity: CVReviewEntity
  )
  suspend fun deleteCVReviewLocal(id: String)

  suspend fun getUserCVReviews(
    token: String
  ): Result<List<CVReviewResponse>>
} 