package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.api.CVReviewApiService
import com.application.elevate.model.CVReviewResponse
import com.application.elevate.model.CVReviewListResponse
import com.application.elevate.data.database.dao.CVReviewDao
import com.application.elevate.data.database.entity.CVReviewEntity
import com.application.elevate.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class CVReviewRepositoryImpl @Inject constructor(
  private val apiService: CVReviewApiService,
  private val cvReviewDao: CVReviewDao
) : CVReviewRepository {

  override suspend fun uploadCV(
    token: String,
    cvFile: File,
    careerField: String
  ): Result<CVReviewResponse> {
    return try {
      Log.d("CVReviewRepo", "Uploading CV: ${cvFile.name}, Career Field: $careerField")
      Log.d("CVReviewRepo", "Token format: ${token.take(20)}...") // Log first 20 chars for debugging
      
      // Prepare file part - pastikan nama file adalah cv.pdf dan field name adalah 'cv'
      val requestFile = cvFile.asRequestBody("application/pdf".toMediaTypeOrNull())
      val filePart = MultipartBody.Part.createFormData("cv", "cv.pdf", requestFile)
      
      // Prepare career field part
      val careerFieldBody = careerField.toRequestBody("text/plain".toMediaTypeOrNull())
      
      // Make API call - token sudah dalam format "Bearer xyz" dari UserRepository
      val response = apiService.uploadCV(
        token = token, // Jangan tambah "Bearer " lagi!
        cv = filePart,
        careerField = careerFieldBody
      )

      // Save to local database for offline access
      val entity = response.toEntity(cvFile.name, careerField)
      cvReviewDao.insertCVReview(entity)
      
      Log.d("CVReviewRepo", "Upload successful: ${response.message}")
      Result.success(response)
      
    } catch (e: Exception) {
      Log.e("CVReviewRepo", "Upload failed", e)
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
    id: String
  ): Result<CVReviewResponse> {
    return try {
      Log.d("CVReviewRepo", "Getting CV review by ID: $id")
      
      val response = apiService.getCVReviewById(token, id)
      
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
} 