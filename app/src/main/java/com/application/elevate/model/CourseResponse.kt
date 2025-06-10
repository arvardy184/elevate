package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class CourseResponse(
    @SerializedName("courses")
    val courses: List<CourseItem>,
    @SerializedName("pagination")
    val pagination: Pagination
)

data class CourseItem(
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
    val category: Category
)

data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class Pagination(
    @SerializedName("total")
    val total: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("totalPages")
    val totalPages: Int
) 