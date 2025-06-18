package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_completions")
data class QuizCompletionEntity(
    @PrimaryKey val id: String, // Kombinasi userId-courseId
    val userId: Int,
    val courseId: Int,
    val score: Int,
    val totalQuestions: Int,
    val isPassed: Boolean,
    val completedAt: Long,
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        fun generateId(userId: Int, courseId: Int): String {
            return "${userId}-${courseId}"
        }
    }
} 