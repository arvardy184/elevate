package com.application.elevate.data.repository

import kotlinx.coroutines.flow.Flow
import com.application.elevate.model.*
import com.application.elevate.data.database.entity.SearchHistoryEntity

interface SearchRepository {
  
  // Course search functionality
  suspend fun getAllCourses(): List<Course>
  suspend fun searchCourses(query: String): List<Course>
  suspend fun getCoursesByCategory(categoryId: Int): List<Course>
  suspend fun getPopularCourses(limit: Int = 10): List<Course>
  
  // Consultant search (using existing CounselingRepository)
  suspend fun searchConsultants(query: String): List<Consultant>
  
  // Search history management
  suspend fun addSearchHistory(query: String, type: String = "course")
  fun getSearchHistory(): Flow<List<SearchHistoryEntity>>
  suspend fun getSearchSuggestions(query: String): List<String>
  suspend fun clearSearchHistory()
  
  // Cache management
  suspend fun getCacheStatus(): SearchCacheStatus
}

// Simple data classes for search
data class SearchCacheStatus(
  val coursesCount: Int,
  val lastSyncTime: Long,
  val isCacheStale: Boolean
) 