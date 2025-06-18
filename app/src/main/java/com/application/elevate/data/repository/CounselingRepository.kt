package com.application.elevate.data.repository

import com.application.elevate.data.database.entity.SearchHistoryEntity
import com.application.elevate.model.*
import kotlinx.coroutines.flow.Flow

interface CounselingRepository {
  // Online API methods
  suspend fun getCounselors(
    page: Int = 1,
    limit: Int = 10,
    specialization: String? = null
  ): Result<ConsultantResponse>
  
  suspend fun getCounselorDetail(counselorId: Int): Result<ConsultantDetailResponse>
  
  // Offline support methods
  fun getCachedConsultants(): Flow<List<Consultant>>
  fun searchConsultants(query: String): Flow<List<Consultant>>
  suspend fun addSearchHistory(query: String, type: String)
  fun getSearchHistory(): Flow<List<SearchHistoryEntity>>
  suspend fun clearSearchHistory()
  
  // Categories management
  suspend fun cacheCategories(categories: List<CounselingCategory>)
  fun getCachedCategories(): Flow<List<CounselingCategory>>
  
  // Cache management
  suspend fun getCacheStats(): CacheStats
}

data class CacheStats(
  val consultantCount: Int,
  val categoryCount: Int
)
