package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_enrollments")
data class CourseEnrollmentEntity(
    @PrimaryKey val courseId: Int,
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) 