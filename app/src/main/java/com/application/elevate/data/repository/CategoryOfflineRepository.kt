package com.application.elevate.data.repository

import com.application.elevate.data.database.entity.CategoryEntity

interface CategoryOfflineRepository {
    suspend fun getAllCategories(): List<CategoryEntity>
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?
    suspend fun saveCategories(categories: List<CategoryEntity>)
    suspend fun saveCategory(category: CategoryEntity)
    suspend fun deleteAllCategories()
    suspend fun getCategoryCount(): Int
} 