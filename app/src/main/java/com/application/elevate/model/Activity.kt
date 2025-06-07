package com.application.elevate.model

data class Activity(
    val id: String,
    val type: String, // "course" or "certificate"
    val title: String,
    val duration: String? = null,
    val lessons: Int? = null,
    val score: Int? = null,
    val imageUrl: String
)