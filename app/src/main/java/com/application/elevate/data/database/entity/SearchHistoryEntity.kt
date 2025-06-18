package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  
  @ColumnInfo(name = "search_query")
  val searchQuery: String,
  
  @ColumnInfo(name = "search_type")
  val searchType: String, // "consultant", "specialization", etc.
  
  @ColumnInfo(name = "search_count")
  val searchCount: Int = 1,
  
  @ColumnInfo(name = "last_searched_at")
  val lastSearchedAt: Long = System.currentTimeMillis()
) 