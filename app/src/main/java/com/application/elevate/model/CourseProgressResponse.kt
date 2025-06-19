package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class CourseProgressResponse(
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("progress")
    val progress: CourseProgress
)

data class CourseProgress(
    @SerializedName("completedLessons")
    val completedLessons: Int,
    @SerializedName("totalLessons")
    val totalLessons: Int,
    @SerializedName("progressPercentage")
    val progressPercentage: Float,
    @SerializedName("lastAccessedAt")
    val lastAccessedAt: String,
    @SerializedName("timeSpent")
    val timeSpent: Int // dalam menit
) 