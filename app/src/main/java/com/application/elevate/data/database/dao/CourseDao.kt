package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.CourseEntity

@Dao
interface CourseDao {

  // Basic CRUD operations
  @Query("SELECT * FROM courses ORDER BY title ASC")
  fun getAllCourses(): Flow<List<CourseEntity>>
  
  @Query("SELECT * FROM courses WHERE category_id = :categoryId ORDER BY rating DESC")
  fun getCoursesByCategory(categoryId: Int): Flow<List<CourseEntity>>
  
  @Query("SELECT * FROM courses WHERE id = :courseId")
  suspend fun getCourseById(courseId: Int): CourseEntity?
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCourse(course: CourseEntity)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCourses(courses: List<CourseEntity>)
  
  @Update
  suspend fun updateCourse(course: CourseEntity)
  
  @Delete
  suspend fun deleteCourse(course: CourseEntity)
  
  // Search functionality
  @Query("""
    SELECT * FROM courses 
    WHERE title LIKE '%' || :query || '%' 
    OR description LIKE '%' || :query || '%'
    OR category_name LIKE '%' || :query || '%'
    OR tags LIKE '%' || :query || '%'
    ORDER BY 
      CASE WHEN title LIKE '%' || :query || '%' THEN 1 ELSE 2 END,
      rating DESC
  """)
  fun searchCourses(query: String): Flow<List<CourseEntity>>
  
  @Query("""
    SELECT * FROM courses 
    WHERE (:categoryId IS NULL OR category_id = :categoryId)
    AND (:level IS NULL OR level = :level) 
    AND (:minRating IS NULL OR rating >= :minRating)
    AND (
      :searchQuery = '' OR 
      title LIKE '%' || :searchQuery || '%' OR 
      description LIKE '%' || :searchQuery || '%' OR
      tags LIKE '%' || :searchQuery || '%'
    )
    ORDER BY 
      CASE 
        WHEN :sortBy = 'title' THEN title
        WHEN :sortBy = 'rating' THEN CAST(rating AS TEXT)
        WHEN :sortBy = 'popularity' THEN CAST(rating_count AS TEXT)
        ELSE CAST(rating AS TEXT)
      END DESC
  """)
  fun searchCoursesWithFilters(
    searchQuery: String,
    categoryId: Int? = null,
    level: String? = null,
    minRating: Float? = null,
    sortBy: String = "rating"
  ): Flow<List<CourseEntity>>
  
  // Popular and featured courses
  @Query("SELECT * FROM courses WHERE is_popular = 1 ORDER BY rating DESC LIMIT :limit")
  fun getPopularCourses(limit: Int = 10): Flow<List<CourseEntity>>
  
  @Query("SELECT * FROM courses WHERE rating >= 4.5 ORDER BY rating_count DESC LIMIT :limit")
  fun getTopRatedCourses(limit: Int = 10): Flow<List<CourseEntity>>
  
  @Query("SELECT * FROM courses ORDER BY created_at DESC LIMIT :limit")
  fun getNewestCourses(limit: Int = 10): Flow<List<CourseEntity>>
  
  // Analytics and counts
  @Query("SELECT COUNT(*) FROM courses")
  suspend fun getCourseCount(): Int
  
  @Query("SELECT COUNT(*) FROM courses WHERE category_id = :categoryId")
  suspend fun getCourseCountByCategory(categoryId: Int): Int
  
  @Query("SELECT DISTINCT category_name FROM courses ORDER BY category_name ASC")
  suspend fun getAllCategories(): List<String>
  
  @Query("SELECT DISTINCT level FROM courses ORDER BY level ASC")
  suspend fun getAllLevels(): List<String>
  
  // Cache management
  @Query("DELETE FROM courses")
  suspend fun clearAllCourses()
  
  @Query("DELETE FROM courses WHERE category_id = :categoryId")
  suspend fun clearCoursesByCategory(categoryId: Int)
  
  @Query("SELECT MAX(updated_at) FROM courses")
  suspend fun getLastUpdateTime(): Long?
} 