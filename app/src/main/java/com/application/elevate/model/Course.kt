package com.application.elevate.model

data class Course(
    val id: String,
    val title: String,
    val duration: String,
    val lessons: Int,
    val progressPercent: Int,
    val rating: Float,
    val ratingCount: Int,
    val imageRes: Int,
    val isLocked: Boolean = false,
    val categoryId: String = ""
)
