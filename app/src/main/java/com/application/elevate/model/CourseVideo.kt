package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class CourseVideoResponse(
    @SerializedName("courseVideos")
    val courseVideos: List<CourseVideoItem>,
    @SerializedName("note")
    val note: String
)

data class CourseVideoItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("videoUrl")
    val videoUrl: String,
    @SerializedName("isLocked")
    val isLocked: Boolean,
    @SerializedName("order")
    val order: Int,
    @SerializedName("s3Key")
    val s3Key: String,
    @SerializedName("originalUrl")
    val originalUrl: String,
    @SerializedName("isProxied")
    val isProxied: Boolean
) 