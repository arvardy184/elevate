package com.application.elevate.model

data class Course(
    val id: Int,
    val title: String,
    val description: String = "",
    val thumbnail: String? = null,
    val categoryId: Int = 0,
    val isPaid: Boolean = false,
    val price: Int = 0,
    val duration: String = "",
    val lessons: Int = 0,
    val progressPercent: Int = 0,
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val imageRes: Int = 0,
    val isLocked: Boolean = false,
    val category: CourseCategory? = null,
    val isEnrolled: Boolean? = null
)

data class CourseCategory(
    val id: Int,
    val name: String
)
