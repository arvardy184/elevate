package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "courses")
data class CourseEntity(
  @PrimaryKey
  val id: Int,
  
  @ColumnInfo(name = "title")
  val title: String,
  
  @ColumnInfo(name = "description")
  val description: String = "",
  
  @ColumnInfo(name = "duration")
  val duration: String,
  
  @ColumnInfo(name = "lessons")
  val lessons: Int,
  
  @ColumnInfo(name = "progress_percent")
  val progressPercent: Int = 0,
  
  @ColumnInfo(name = "rating")
  val rating: Float,
  
  @ColumnInfo(name = "rating_count")
  val ratingCount: Int,
  
  @ColumnInfo(name = "image_url")
  val imageUrl: String? = null,
  
  @ColumnInfo(name = "image_res")
  val imageRes: Int? = null,
  
  @ColumnInfo(name = "is_locked")
  val isLocked: Boolean = false,
  
  @ColumnInfo(name = "is_popular")
  val isPopular: Boolean = false,
  
  @ColumnInfo(name = "category_id")
  val categoryId: Int,
  
  @ColumnInfo(name = "category_name")
  val categoryName: String = "",
  
  @ColumnInfo(name = "price")
  val price: Int = 0,
  
  @ColumnInfo(name = "instructor_name")
  val instructorName: String? = null,
  
  @ColumnInfo(name = "level")
  val level: String = "Beginner",
  
  @ColumnInfo(name = "tags")
  val tags: String = "[]", // JSON array of searchable tags
  
  @ColumnInfo(name = "created_at")
  val createdAt: Long = System.currentTimeMillis(),
  
  @ColumnInfo(name = "updated_at")
  val updatedAt: Long = System.currentTimeMillis()
) 