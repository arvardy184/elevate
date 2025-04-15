package com.application.elevate.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Consultant(
    val id: String,
    val name: String,
    val title: String,
    val rating: Float,
    val reviews: Int,
    val price: Int,
    val image: Int,
    val about: String?=null,
)