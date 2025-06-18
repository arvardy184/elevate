package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {

  @Query("SELECT * FROM search_history ORDER BY last_searched_at DESC LIMIT :limit")
  fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryEntity>>
  
  @Query("SELECT * FROM search_history WHERE search_type = :type ORDER BY search_count DESC, last_searched_at DESC LIMIT :limit")
  fun getTopSearchesByType(type: String, limit: Int = 5): Flow<List<SearchHistoryEntity>>
  
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertSearch(search: SearchHistoryEntity): Long
  
  @Query("UPDATE search_history SET search_count = search_count + 1, last_searched_at = :timestamp WHERE search_query = :query AND search_type = :type")
  suspend fun updateSearchCount(query: String, type: String, timestamp: Long = System.currentTimeMillis()): Int
  
  @Transaction
  suspend fun insertOrUpdateSearch(search: SearchHistoryEntity) {
    val updatedRows = updateSearchCount(search.searchQuery, search.searchType)
    if (updatedRows == 0) {
      insertSearch(search)
    }
  }
  
  @Delete
  suspend fun deleteSearch(search: SearchHistoryEntity)
  
  @Query("DELETE FROM search_history WHERE last_searched_at < :timestamp")
  suspend fun deleteOldSearches(timestamp: Long): Int
  
  @Query("DELETE FROM search_history")
  suspend fun clearAllSearchHistory()
} 