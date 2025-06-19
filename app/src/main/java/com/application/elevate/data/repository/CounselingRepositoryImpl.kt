package com.application.elevate.data.repository

import com.application.elevate.data.api.CounselingApiService
import com.application.elevate.data.database.dao.ConsultantDao
import com.application.elevate.data.database.dao.CounselingCategoryDao
import com.application.elevate.data.database.dao.SearchHistoryDao
import com.application.elevate.data.database.entity.SearchHistoryEntity
import com.application.elevate.data.mapper.ConsultantMapper
import com.application.elevate.data.mapper.CounselingCategoryMapper
import com.application.elevate.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounselingRepositoryImpl @Inject constructor(
  private val apiService: CounselingApiService,
  private val consultantDao: ConsultantDao,
  private val categoryDao: CounselingCategoryDao,
  private val searchHistoryDao: SearchHistoryDao
) : CounselingRepository {

  companion object {
    private const val CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000L // 24 hours
  }
  
  override suspend fun getCounselors(
    page: Int,
    limit: Int,
    specialization: String?
  ): Result<ConsultantResponse> {
    return try {
      // 1. Coba ambil dari cache dulu (offline-first approach)
      val cachedConsultants = if (specialization != null) {
        consultantDao.getConsultantsBySpecialization(specialization).first()
      } else {
        consultantDao.getAllConsultants().first()
      }

      // 2. Kalau ada cache dan masih fresh, return cache
      if (cachedConsultants.isNotEmpty() && isCacheFresh(cachedConsultants.first().cachedAt)) {
        val consultants = ConsultantMapper.fromEntityList(cachedConsultants)
        return Result.success(
          ConsultantResponse(
            success = true,
            data = consultants,
            pagination = CounselingPagination(
              currentPage = page,
              totalPages = 1,
              totalItems = consultants.size,
              itemsPerPage = limit
            )
          )
        )
      }

      // 3. Kalau cache gak ada atau expired, fetch from API
      val response = apiService.getCounselors(page, limit, specialization)
      
      // 4. Save ke database untuk next time
      val consultantEntities = ConsultantMapper.toEntityList(response.data)
      consultantDao.insertConsultants(consultantEntities)

      // 5. Clean up old cache
      cleanupStaleCache()

      Result.success(response)
    } catch (e: Exception) {
      // Fallback ke cache kalau network error
      try {
        val cachedConsultants = if (specialization != null) {
          consultantDao.getConsultantsBySpecialization(specialization).first()
        } else {
          consultantDao.getAllConsultants().first()
        }

        if (cachedConsultants.isNotEmpty()) {
          val consultants = ConsultantMapper.fromEntityList(cachedConsultants)
          Result.success(
            ConsultantResponse(
              success = true,
              data = consultants,
              pagination = CounselingPagination(
                currentPage = page,
                totalPages = 1,
                totalItems = consultants.size,
                itemsPerPage = limit
              )
            )
          )
        } else {
          Result.failure(e)
        }
      } catch (cacheException: Exception) {
        Result.failure(e)
      }
    }
  }
  
  override suspend fun getCounselorDetail(counselorId: Int): Result<ConsultantDetailResponse> {
    return try {
      // 1. Check cache first
      val cachedConsultant = consultantDao.getConsultantById(counselorId)
      
      if (cachedConsultant != null && isCacheFresh(cachedConsultant.cachedAt)) {
        return Result.success(
          ConsultantDetailResponse(
            success = true,
            data = ConsultantMapper.fromEntity(cachedConsultant)
          )
        )
      }

      // 2. Fetch from API
      val response = apiService.getCounselorDetail(counselorId)
      
      // 3. Update cache
      val consultantEntity = ConsultantMapper.toEntity(response.data)
        .copy(lastUpdated = System.currentTimeMillis())
      consultantDao.insertConsultant(consultantEntity)

      Result.success(response)
    } catch (e: Exception) {
      // Fallback to cache
      try {
        val cachedConsultant = consultantDao.getConsultantById(counselorId)
        if (cachedConsultant != null) {
          Result.success(
            ConsultantDetailResponse(
              success = true,
              data = ConsultantMapper.fromEntity(cachedConsultant)
            )
          )
        } else {
          Result.failure(e)
        }
      } catch (cacheException: Exception) {
        Result.failure(e)
      }
    }
  }

  // Additional methods for offline support
  override fun getCachedConsultants(): Flow<List<Consultant>> {
    return consultantDao.getAllConsultants()
      .map { ConsultantMapper.fromEntityList(it) }
  }

  override fun searchConsultants(query: String): Flow<List<Consultant>> {
    return consultantDao.searchConsultants(query)
      .map { ConsultantMapper.fromEntityList(it) }
  }

  override suspend fun addSearchHistory(query: String, type: String) {
    val searchHistory = SearchHistoryEntity(
      searchQuery = query,
      searchType = type
    )
//    searchHistoryDao.insertOrUpdateSearch(searchHistory)
  }

  override fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
    return searchHistoryDao.getRecentSearches()
  }

  override suspend fun clearSearchHistory() {
    searchHistoryDao.clearAllSearchHistory()
  }

  // Categories management
  override suspend fun cacheCategories(categories: List<CounselingCategory>) {
    val categoryEntities = CounselingCategoryMapper.toEntityList(categories)
    categoryDao.insertCategories(categoryEntities)
  }

  override fun getCachedCategories(): Flow<List<CounselingCategory>> {
    return categoryDao.getAllCategories()
      .map { CounselingCategoryMapper.fromEntityList(it) }
  }

  // Cache management helper methods
  private fun isCacheFresh(cachedTime: Long): Boolean {
    return System.currentTimeMillis() - cachedTime < CACHE_EXPIRY_TIME
  }

  private suspend fun cleanupStaleCache() {
    val staleTimestamp = System.currentTimeMillis() - CACHE_EXPIRY_TIME
    consultantDao.deleteStaleConsultants(staleTimestamp)
    searchHistoryDao.deleteOldSearches(staleTimestamp)
  }

  override suspend fun getCacheStats(): CacheStats {
    return CacheStats(
      consultantCount = consultantDao.getConsultantCount(),
      categoryCount = categoryDao.getCategoryCount()
    )
  }
} 