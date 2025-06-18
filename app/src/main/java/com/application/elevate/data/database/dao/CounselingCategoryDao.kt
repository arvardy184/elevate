package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.CounselingCategoryEntity

@Dao
interface CounselingCategoryDao {

  @Query("SELECT * FROM counseling_category ORDER BY name ASC")
  fun getAllCategories(): Flow<List<CounselingCategoryEntity>>
  
  @Query("SELECT * FROM counseling_category WHERE id = :categoryId")
  suspend fun getCategoryById(categoryId: String): CounselingCategoryEntity?
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CounselingCategoryEntity>)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CounselingCategoryEntity)
  
  @Delete
  suspend fun deleteCategory(category: CounselingCategoryEntity)
  
  @Query("DELETE FROM counseling_category")
  suspend fun deleteAllCategories()
  
  @Query("SELECT COUNT(*) FROM counseling_category")
  suspend fun getCategoryCount(): Int
} 