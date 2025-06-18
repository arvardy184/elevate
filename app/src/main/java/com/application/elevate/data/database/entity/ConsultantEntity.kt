package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "consultant")
data class ConsultantEntity(
  @PrimaryKey 
  val id: Int,
  
  @ColumnInfo(name = "user_id")
  val userId: Int,
  
  val specialization: String,
  val bio: String,
  val verified: Boolean,
  
  // CounselorUser fields
  @ColumnInfo(name = "first_name")
  val firstName: String,
  
  @ColumnInfo(name = "last_name") 
  val lastName: String,
  
  val email: String,
  
  // Session and rating data
  @ColumnInfo(name = "total_sessions")
  val totalSessions: Int,
  
  @ColumnInfo(name = "average_rating")
  val averageRating: Double,
  
  @ColumnInfo(name = "session_count")
  val sessionCount: Int,
  
  // Cache metadata
  @ColumnInfo(name = "cached_at")
  val cachedAt: Long = System.currentTimeMillis(),
  
  @ColumnInfo(name = "last_updated")
  val lastUpdated: Long = System.currentTimeMillis()
) 