package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.application.elevate.data.database.converter.StringListConverter

@Entity(tableName = "course_quizzes")
data class CourseQuizEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val question: String,
    @TypeConverters(StringListConverter::class)
    val options: List<String>,
    val correctAnswer: String,
    val isLocked: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
) 