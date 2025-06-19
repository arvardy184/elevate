package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_answers")
data class QuizAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val courseId: Int,
    val quizId: Int,
    val questionId: Int,
    val answer: String,
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) 