package com.application.elevate.data.repository

import com.application.elevate.model.CVReviewResponse
import com.application.elevate.model.CVReviewListResponse
import com.application.elevate.data.database.entity.CVReviewEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

interface CVReviewRepository {
  suspend fun uploadCV(
    token: String,
    cvFile: File,
    careerField: String
  ): Result<CVReviewResponse>

  suspend fun getMyCVReviews(
    token: String,
    page: Int = 1,
    limit: Int = 10
  ): Result<CVReviewListResponse>

  suspend fun getCVReviewById(
    token: String,
    id: String
  ): Result<CVReviewResponse>

  suspend fun updateCVReview(
    token: String,
    id: String,
    careerField: String
  ): Result<CVReviewResponse>

  suspend fun deleteCVReview(
    token: String,
    id: String
  ): Result<CVReviewResponse>
  
  // Offline support methods
  fun getAllCVReviewsLocal(): Flow<List<CVReviewEntity>>
  suspend fun getCVReviewByIdLocal(id: String): CVReviewEntity?
  suspend fun saveCVReviewLocal(entity: CVReviewEntity)
  suspend fun deleteCVReviewLocal(id: String)
} 