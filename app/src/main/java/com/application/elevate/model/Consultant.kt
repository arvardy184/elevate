package com.application.elevate.model

data class Consultant(
    val id: String,
    val name: String,
    val title: String,
    val rating: Float,
    val reviewCount: Int,
    val categoryId: String, // atau categoryName: String
    val price: Int,
    val oldPrice: Int,
    val imageResId: Int // drawable resource for dummy image
)