package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class AssessmentResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: AssessmentData
)

data class AssessmentData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("questions")
    val questions: List<Question>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class Question(
    @SerializedName("id")
    val id: Int,
    @SerializedName("question")
    val question: String,
    @SerializedName("options")
    val options: List<Option>
)

data class Option(
    @SerializedName("id")
    val id: Int,
    @SerializedName("option")
    val option: String,
    @SerializedName("score")
    val score: Int
) 