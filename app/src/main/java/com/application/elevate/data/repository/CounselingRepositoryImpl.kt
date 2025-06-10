package com.application.elevate.data.repository

import com.application.elevate.data.api.CounselingApiService
import com.application.elevate.model.ConsultantResponse
import com.application.elevate.model.ConsultantDetailResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounselingRepositoryImpl @Inject constructor(
  private val apiService: CounselingApiService
) : CounselingRepository {
  
  override suspend fun getCounselors(
    page: Int,
    limit: Int,
    specialization: String?
  ): Result<ConsultantResponse> {
    return try {
      val response = apiService.getCounselors(page, limit, specialization)
      Result.success(response)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
  
  override suspend fun getCounselorDetail(counselorId: Int): Result<ConsultantDetailResponse> {
    return try {
      val response = apiService.getCounselorDetail(counselorId)
      Result.success(response)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
} 