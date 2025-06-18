package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class CourseQuizResponse(
    @SerializedName("quizzes")
    val quizzes: List<CourseQuizItem>
)

data class CourseQuizItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("question")
    val question: String,
    @SerializedName("options")
    val options: List<String>,
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    @SerializedName("isLocked")
    val isLocked: Boolean
)

data class QuizSubmitRequest(
    @SerializedName("answers")
    val answers: List<String>
)

data class QuizSubmitResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("totalQuestions")
    val totalQuestions: Int,
    @SerializedName("isPassed")
    val isPassed: Boolean
)

// Model untuk error response API
data class ApiErrorResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("errors")
    val errors: Map<String, List<String>>?
) 