package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
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
    val isEnrolled: Boolean = false,
    val isDownloaded: Boolean = false, // Untuk track apakah course sudah didownload lengkap
    val isSynced: Boolean = true, // Untuk track data yang belum sync ke server
    val lastUpdated: Long = System.currentTimeMillis()
) 