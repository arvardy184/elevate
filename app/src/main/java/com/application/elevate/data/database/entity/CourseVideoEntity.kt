package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_videos")
data class CourseVideoEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val title: String,
    val videoUrl: String,
    val isLocked: Boolean,
    val order: Int,
    val s3Key: String,
    val originalUrl: String,
    val isProxied: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
) 