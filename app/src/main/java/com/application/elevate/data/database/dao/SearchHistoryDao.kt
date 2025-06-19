package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {

  // Basic search history operations
  @Query("SELECT * FROM search_history ORDER BY search_timestamp DESC LIMIT :limit")
  fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryEntity>>
  
  @Query("SELECT * FROM search_history WHERE search_type = :type ORDER BY search_timestamp DESC LIMIT :limit")
  fun getRecentSearchesByType(type: String, limit: Int = 10): Flow<List<SearchHistoryEntity>>
  
  @Query("SELECT DISTINCT search_query FROM search_history WHERE search_query LIKE '%' || :query || '%' ORDER BY search_timestamp DESC LIMIT :limit")
  suspend fun getSearchSuggestions(query: String, limit: Int = 5): List<String>
  
  // Insert/Update operations
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity)
  
  @Query("""
    INSERT OR REPLACE INTO search_history 
    (search_query, search_type, category_filter, result_count, search_timestamp, search_duration_ms)
    VALUES (:query, :type, :categoryFilter, :resultCount, :timestamp, :duration)
  """)
  suspend fun insertOrUpdateSearch(
    query: String,
    type: String,
    categoryFilter: String? = null,
    resultCount: Int = 0,
    timestamp: Long = System.currentTimeMillis(),
    duration: Long = 0
  )
  
  @Query("UPDATE search_history SET selected_result_id = :resultId WHERE id = :searchId")
  suspend fun updateSelectedResult(searchId: Long, resultId: String)
  
  // Analytics and insights - simplified for now
  @Query("SELECT COUNT(*) FROM search_history WHERE search_type = :type")
  suspend fun getSearchCountByType(type: String): Int
  
  @Query("SELECT COUNT(*) FROM search_history WHERE search_timestamp > :since")
  suspend fun getRecentSearchCount(since: Long): Int
  
  // Cleanup operations
  @Delete
  suspend fun deleteSearchHistory(searchHistory: SearchHistoryEntity)
  
  @Query("DELETE FROM search_history WHERE search_query = :query AND search_type = :type")
  suspend fun deleteSearchByQuery(query: String, type: String)
  
  @Query("DELETE FROM search_history")
  suspend fun clearAllSearchHistory()
  
  @Query("DELETE FROM search_history WHERE search_timestamp < :timestamp")
  suspend fun deleteOldSearches(timestamp: Long): Int
  
  @Query("SELECT COUNT(*) FROM search_history")
  suspend fun getSearchHistoryCount(): Int
} 