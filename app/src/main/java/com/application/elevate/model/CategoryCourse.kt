package com.application.elevate.model

data class CategoryCourse(
    val id: String,
    val title: String,
    val duration: String,
    val lessons: Int, 
    val progressPercent: Int,
    val imageRes: Int,
    val isCompleted: Boolean = false,
    val categoryId: String = ""
) 