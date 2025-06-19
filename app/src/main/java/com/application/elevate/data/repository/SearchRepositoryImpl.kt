package com.application.elevate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.application.elevate.model.*
import com.application.elevate.data.database.entity.SearchHistoryEntity
import com.application.elevate.data.database.dao.*
import com.application.elevate.data.mapper.CourseMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
  private val courseApiService: com.application.elevate.data.api.CourseApiService,
  private val courseDao: CourseDao,
  private val consultantDao: ConsultantDao,
  private val searchHistoryDao: SearchHistoryDao,
  private val counselingRepository: CounselingRepository
) : SearchRepository {

  companion object {
    private const val CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000L // 24 hours
  }

  override suspend fun getAllCourses(): List<Course> {
    return try {
      // Try API first
      val response = courseApiService.getAllCourses()
      // Cache the results
      val courseEntities = response.courses.map { course ->
        val categoryName = course.category?.name ?: "Unknown"
        CourseMapper.toEntity(course, categoryName)
      }
      courseDao.insertCourses(courseEntities)
      response.courses
    } catch (e: Exception) {
      // Network error - fallback to cache
      getCachedCourses()
    }
  }

  private suspend fun getCachedCourses(): List<Course> {
    return try {
      courseDao.getAllCourses().first().let { entities ->
        CourseMapper.fromEntityList(entities)
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun searchCourses(query: String): List<Course> {
    return try {
      if (query.isBlank()) {
        // If no query, get all courses
        getAllCourses()
      } else {
        // Search using API with query parameter
        android.util.Log.d("SearchRepository", "Searching courses with query: $query")
        val response = courseApiService.searchCourses(
          searchQuery = query,
          page = 1,
          limit = 50
        )
        
        // Debug logging
        android.util.Log.d("SearchRepository", "API Response received:")
        android.util.Log.d("SearchRepository", "- Courses count: ${response.courses.size}")
        android.util.Log.d("SearchRepository", "- Pagination: total=${response.pagination.total}, page=${response.pagination.page}")
        
        if (response.courses.isNotEmpty()) {
          android.util.Log.d("SearchRepository", "- First course: ${response.courses[0].title}")
          response.courses.forEach { course ->
            android.util.Log.d("SearchRepository", "  * Course: ${course.title}, ID: ${course.id}, Category: ${course.category?.name}")
          }
        } else {
          android.util.Log.w("SearchRepository", "No courses found in response!")
        }
        
        // Skip caching for now - just return API results
        android.util.Log.d("SearchRepository", "Returning ${response.courses.size} courses from API (no caching)")
        return response.courses
      }
    } catch (e: Exception) {
      // Network error - fallback to local search
      android.util.Log.e("SearchRepository", "API search failed, falling back to local search", e)
      val localResults = searchCoursesLocally(query)
      android.util.Log.d("SearchRepository", "Local search returned ${localResults.size} courses")
      localResults
    }
  }

  private suspend fun searchCoursesLocally(query: String): List<Course> {
    return try {
      courseDao.searchCourses(query).first().let { entities ->
        CourseMapper.fromEntityList(entities)
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun getCoursesByCategory(categoryId: Int): List<Course> {
    return try {
      courseDao.getCoursesByCategory(categoryId).first().let { entities ->
        CourseMapper.fromEntityList(entities)
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun getPopularCourses(limit: Int): List<Course> {
    return try {
      // Try API first
      val response = courseApiService.getPopularCourses(limit)
      // Cache popular courses
      val courseEntities = response.courses.map { course ->
        val categoryName = course.category?.name ?: "Unknown"
        CourseMapper.toEntity(course, categoryName)
      }
      courseDao.insertCourses(courseEntities)
      response.courses
    } catch (e: Exception) {
      // Network error - fallback to cache
      getPopularCoursesLocally(limit)
    }
  }

  private suspend fun getPopularCoursesLocally(limit: Int): List<Course> {
    return try {
      courseDao.getPopularCourses(limit).first().let { entities ->
        CourseMapper.fromEntityList(entities)
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun searchConsultants(query: String): List<Consultant> {
    return try {
      if (query.isBlank()) {
        counselingRepository.getCachedConsultants().first()
      } else {
        counselingRepository.searchConsultants(query).first()
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun addSearchHistory(query: String, type: String) {
    if (query.isNotBlank()) {
      try {
        searchHistoryDao.insertOrUpdateSearch(
          query = query.trim(),
          type = type,
          resultCount = 0 // Will be updated when we track results
        )
      } catch (e: Exception) {
        // Silently fail for now
      }
    }
  }

  override fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
    return searchHistoryDao.getRecentSearches(10)
  }

  override suspend fun getSearchSuggestions(query: String): List<String> {
    return try {
      searchHistoryDao.getSearchSuggestions(query, 5)
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun clearSearchHistory() {
    try {
      searchHistoryDao.clearAllSearchHistory()
    } catch (e: Exception) {
      // Silently fail for now
    }
  }



  override suspend fun getCacheStatus(): SearchCacheStatus {
    return try {
      val coursesCount = courseDao.getCourseCount()
      val lastSyncTime = courseDao.getLastUpdateTime() ?: 0L
      val isCacheStale = (System.currentTimeMillis() - lastSyncTime) > CACHE_EXPIRY_TIME
      
      SearchCacheStatus(
        coursesCount = coursesCount,
        lastSyncTime = lastSyncTime,
        isCacheStale = isCacheStale
      )
    } catch (e: Exception) {
      SearchCacheStatus(0, 0L, true)
    }
  }
} 