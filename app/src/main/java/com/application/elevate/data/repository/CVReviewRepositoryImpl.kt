package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.CVReviewApiService
import com.application.elevate.model.CVReviewResponse
import com.application.elevate.model.CVReviewListResponse
import com.application.elevate.model.CVReviewDetailResponse
import com.application.elevate.data.database.dao.CVReviewDao
import com.application.elevate.data.database.entity.CVReviewEntity
import com.application.elevate.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CVReviewRepositoryImpl @Inject constructor(
  private val apiService: CVReviewApiService,
  private val cvReviewDao: CVReviewDao
) : CVReviewRepository {

  override suspend fun uploadCV(
    token: String,
    cvFile: MultipartBody.Part,
    careerField: RequestBody
  ): Result<CVReviewResponse> {
    return try {
      Log.d("CVReviewRepo", "Uploading CV with token: ${token.take(20)}...")
      Log.d("CVReviewRepo", "Token starts with Bearer: ${token.startsWith("Bearer ")}")
      Log.d("CVReviewRepo", "File part name: ${cvFile.headers?.get("Content-Disposition")}")
      
      // Make API call
      val response = apiService.uploadCV(
        token = token,
        cv = cvFile,
        careerField = careerField
      )

      // Save to local database for offline access
      val fileName = "cv.pdf" // Default name
      val careerFieldStr = "Unknown" // Default field
      val entity = response.toEntity(fileName, careerFieldStr)
      cvReviewDao.insertCVReview(entity)
      
      Log.d("CVReviewRepo", "Upload successful: ${response.message}")
      Result.success(response)
      
    } catch (e: Exception) {
      Log.e("CVReviewRepo", "Upload failed", e)
      Log.e("CVReviewRepo", "Exception type: ${e::class.java.simpleName}")
      if (e is retrofit2.HttpException) {
        Log.e("CVReviewRepo", "HTTP Error Code: ${e.code()}")
        Log.e("CVReviewRepo", "HTTP Error Body: ${e.response()?.errorBody()?.string()}")
      }
      Result.failure(e)
    }
  }

  override suspend fun getMyCVReviews(
    token: String,
    page: Int,
    limit: Int
  ): Result<CVReviewListResponse> {
    return try {
      Log.d("CVReviewRepo", "Getting my CV reviews - page: $page, limit: $limit")
      
      val response = apiService.getMyCVReviews(token, page, limit)
      
      // Sync API data to Room for offline access
      if (page == 1) { // Only clear and sync on first page
        val entities = response.data.map { it.toEntity() }
        cvReviewDao.insertAllCVReviews(entities)
        Log.d("CVReviewRepo", "Synced ${entities.size} CV reviews to local database")
      }
      
      Log.d("CVReviewRepo", "Get reviews successful: ${response.data.size} items")
      Result.success(response)
      
    } catch (e: Exception) {
      Log.e("CVReviewRepo", "Get reviews failed", e)
      Result.failure(e)
    }
  }

  override suspend fun getCVReviewById(
    token: String,
    reviewId: String
  ): Result<CVReviewDetailResponse> {
    return try {
      Log.d("CVReviewRepo", "Getting CV review by ID: $reviewId")
      
      val response = apiService.getCVReviewById(token, reviewId)
      
      Log.d("CVReviewRepo", "Get review by ID successful")
      Result.success(response)
      
    } catch (e: Exception) {
      Log.e("CVReviewRepo", "Get review by ID failed", e)
      Result.failure(e)
    }
  }

  override suspend fun updateCVReview(
    token: String,
    id: String,
    careerField: String
  ): Result<CVReviewResponse> {
    return try {
      Log.d("CVReviewRepo", "Updating CV review: $id with career field: $careerField")
      
      val response = apiService.updateCVReview(token, id, careerField)
      
      Log.d("CVReviewRepo", "Update successful: ${response.message}")
      Result.success(response)
      
    } catch (e: Exception) {
      Log.e("CVReviewRepo", "Update failed", e)
      Result.failure(e)
    }
  }

  override suspend fun deleteCVReview(
    token: String,
    id: String
  ): Result<CVReviewResponse> {
    return try {
      Log.d("CVReviewRepo", "Deleting CV review: $id")
      
      val response = apiService.deleteCVReview(token, id)
      
      // Also delete from local database
      cvReviewDao.deleteCVReviewById(id)
      
      Log.d("CVReviewRepo", "Delete successful: ${response.message}")
      Result.success(response)
      
    } catch (e: Exception) {
      Log.e("CVReviewRepo", "Delete failed", e)
      Result.failure(e)
    }
  }
  
  // Offline support methods
  override fun getAllCVReviewsLocal(): Flow<List<CVReviewEntity>> {
    return cvReviewDao.getAllCVReviews()
  }
  
  override suspend fun getCVReviewByIdLocal(id: String): CVReviewEntity? {
    return cvReviewDao.getCVReviewById(id)
  }
  
  override suspend fun saveCVReviewLocal(entity: CVReviewEntity) {
    cvReviewDao.insertCVReview(entity)
  }
  
  override suspend fun deleteCVReviewLocal(id: String) {
    cvReviewDao.deleteCVReviewById(id)
  }

  override suspend fun getUserCVReviews(
    token: String
  ): Result<List<CVReviewResponse>> {
    return try {
      Log.d("CVReviewRepo", "Getting user CV reviews")
      
      // Get first page with high limit to get all reviews
      val response = apiService.getMyCVReviews(token, 1, 100)
      
      // Convert to List<CVReviewResponse> format expected by interface
      val cvReviews = response.data.map { item ->
        CVReviewResponse(
          status = "success",
          message = "CV review retrieved",
          data = com.application.elevate.model.CVReviewData(
            id = item.id,
            fileName = item.fileName,
            careerField = item.careerField,
            fileUrl = "",
            fileSize = 0,
            scores = com.application.elevate.model.CVScores(
              relevancyRate = item.relevancyRate,
              targetedJobRate = item.targetedJobRate,
              overallScore = item.overallScore,
              relevantSkill = 0.0,
              workExperience = 0.0,
              consistency = 0.0,
              writingQuality = 0.0
            ),
            aiAnalysis = com.application.elevate.model.AIAnalysis(
              summary = "",
              strengths = listOf(),
              weaknesses = listOf(),
              careerFieldFit = ""
            ),
            suggestions = listOf(),
            createdAt = item.createdAt
          )
        )
      }
      
      Log.d("CVReviewRepo", "Get user reviews successful: ${cvReviews.size} items")
      Result.success(cvReviews)
      
    } catch (e: Exception) {
      Log.e("CVReviewRepo", "Get user reviews failed", e)
      Result.failure(e)
    }
  }
} 