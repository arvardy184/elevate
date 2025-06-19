package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class CourseDetailResponse(
    @SerializedName("course")
    val course: CourseDetailItem
)

data class CourseDetailItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("isPaid")
    val isPaid: Boolean,
    @SerializedName("price")
    val price: Int,
    @SerializedName("createdById")
    val createdById: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("category")
    val category: CourseCategory,
    @SerializedName("averageRating")
    val averageRating: Float,
    @SerializedName("totalReviews")
    val totalReviews: Int,
    @SerializedName("isEnrolled")
    val isEnrolled: Boolean = false
)

data class CourseCategory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) 