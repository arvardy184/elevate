package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  
  @ColumnInfo(name = "search_query")
  val searchQuery: String,
  
  @ColumnInfo(name = "search_type")
  val searchType: String, // "course", "consultant", "content", "all"
  
  @ColumnInfo(name = "category_filter")
  val categoryFilter: String? = null,
  
  @ColumnInfo(name = "result_count")
  val resultCount: Int = 0,
  
  @ColumnInfo(name = "selected_result_id")
  val selectedResultId: String? = null, // Track what user actually clicked
  
  @ColumnInfo(name = "search_timestamp")
  val searchTimestamp: Long = System.currentTimeMillis(),
  
  @ColumnInfo(name = "search_duration_ms")
  val searchDurationMs: Long = 0 // Track how long search took
) 