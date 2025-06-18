package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_details")
data class CourseDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val thumbnail: String,
    val categoryId: Int,
    val isPaid: Boolean,
    val price: Int,
    val createdById: Int,
    val createdAt: String,
    val categoryName: String,
    val averageRating: Float,
    val totalReviews: Int,
    val isDownloaded: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) 