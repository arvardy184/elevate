package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) 