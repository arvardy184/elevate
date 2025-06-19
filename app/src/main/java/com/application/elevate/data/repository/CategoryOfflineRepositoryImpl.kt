package com.application.elevate.data.repository

import com.application.elevate.data.database.dao.CategoryDao
import com.application.elevate.data.database.entity.CategoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryOfflineRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryOfflineRepository {
    
    override suspend fun getAllCategories(): List<CategoryEntity> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getCategoryById(categoryId: Int): CategoryEntity? {
        return categoryDao.getCategoryById(categoryId)
    }

    override suspend fun saveCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }

    override suspend fun saveCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    override suspend fun deleteAllCategories() {
        categoryDao.deleteAllCategories()
    }

    override suspend fun getCategoryCount(): Int {
        return categoryDao.getCategoryCount()
    }
} 