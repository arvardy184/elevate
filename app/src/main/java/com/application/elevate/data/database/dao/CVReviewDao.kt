package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.CVReviewEntity

@Dao
interface CVReviewDao {
  
  @Query("SELECT * FROM cv_reviews ORDER BY uploadDate DESC")
  fun getAllCVReviews(): Flow<List<CVReviewEntity>>
  
  @Query("SELECT * FROM cv_reviews WHERE id = :id")
  suspend fun getCVReviewById(id: String): CVReviewEntity?
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCVReview(cvReview: CVReviewEntity)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllCVReviews(cvReviews: List<CVReviewEntity>)
  
  @Update
  suspend fun updateCVReview(cvReview: CVReviewEntity)
  
  @Delete
  suspend fun deleteCVReview(cvReview: CVReviewEntity)
  
  @Query("DELETE FROM cv_reviews WHERE id = :id")
  suspend fun deleteCVReviewById(id: String)
  
  @Query("DELETE FROM cv_reviews")
  suspend fun deleteAllCVReviews()
  
  @Query("SELECT COUNT(*) FROM cv_reviews")
  suspend fun getReviewCount(): Int
}