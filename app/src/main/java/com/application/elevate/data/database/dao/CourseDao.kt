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
import com.application.elevate.data.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    // Course operations
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Int): CourseEntity?

    @Query("SELECT * FROM courses WHERE isEnrolled = 1")
    suspend fun getEnrolledCourses(): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE isSynced = 0")
    suspend fun getUnsyncedCourses(): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("UPDATE courses SET isEnrolled = 1, isSynced = :isSynced WHERE id = :courseId")
    suspend fun enrollCourse(courseId: Int, isSynced: Boolean = false)

    @Query("UPDATE courses SET isDownloaded = 1 WHERE id = :courseId")
    suspend fun markCourseAsDownloaded(courseId: Int)

    @Query("UPDATE courses SET isSynced = 1 WHERE id = :courseId")
    suspend fun markCourseAsSynced(courseId: Int)

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()

    // Course Detail operations
    @Query("SELECT * FROM course_details WHERE id = :courseId")
    suspend fun getCourseDetailById(courseId: Int): CourseDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseDetail(courseDetail: CourseDetailEntity)

    @Query("DELETE FROM course_details WHERE id = :courseId")
    suspend fun deleteCourseDetail(courseId: Int)

    // Course Video operations
    @Query("SELECT * FROM course_videos WHERE courseId = :courseId ORDER BY `order` ASC")
    suspend fun getCourseVideos(courseId: Int): List<CourseVideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseVideos(videos: List<CourseVideoEntity>)

    @Query("DELETE FROM course_videos WHERE courseId = :courseId")
    suspend fun deleteCourseVideos(courseId: Int)

    // Course Quiz operations
    @Query("SELECT * FROM course_quizzes WHERE courseId = :courseId")
    suspend fun getCourseQuizzes(courseId: Int): List<CourseQuizEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseQuizzes(quizzes: List<CourseQuizEntity>)

    @Query("DELETE FROM course_quizzes WHERE courseId = :courseId")
    suspend fun deleteCourseQuizzes(courseId: Int)

    // Quiz Answer operations
    @Query("SELECT * FROM quiz_answers WHERE userId = :userId AND courseId = :courseId AND isSynced = 0")
    suspend fun getUnsyncedQuizAnswers(userId: Int, courseId: Int): List<QuizAnswerEntity>

    @Query("SELECT * FROM quiz_answers WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedQuizAnswers(userId: Int): List<QuizAnswerEntity>

    @Query("SELECT * FROM quiz_answers WHERE userId = :userId AND courseId = :courseId")
    suspend fun getQuizAnswers(userId: Int, courseId: Int): List<QuizAnswerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAnswers(answers: List<QuizAnswerEntity>)

    @Query("UPDATE quiz_answers SET isSynced = 1 WHERE userId = :userId AND courseId = :courseId")
    suspend fun markQuizAnswersAsSynced(userId: Int, courseId: Int)

    @Query("DELETE FROM quiz_answers WHERE userId = :userId AND courseId = :courseId")
    suspend fun deleteQuizAnswers(userId: Int, courseId: Int)
    
    // Quiz Completion operations
    @Query("SELECT * FROM quiz_completions WHERE userId = :userId AND courseId = :courseId")
    suspend fun getQuizCompletion(userId: Int, courseId: Int): QuizCompletionEntity?
    
    @Query("SELECT * FROM quiz_completions WHERE userId = :userId")
    suspend fun getAllQuizCompletions(userId: Int): List<QuizCompletionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizCompletion(completion: QuizCompletionEntity)
    
    @Query("UPDATE quiz_completions SET isSynced = 1 WHERE userId = :userId AND courseId = :courseId")
    suspend fun markQuizCompletionAsSynced(userId: Int, courseId: Int)
    
    @Query("DELETE FROM quiz_completions WHERE userId = :userId AND courseId = :courseId")
    suspend fun deleteQuizCompletion(userId: Int, courseId: Int)

    // Clear all quiz data for a specific user (untuk saat logout)
    @Query("DELETE FROM quiz_answers WHERE userId = :userId")
    suspend fun clearUserQuizAnswers(userId: Int)
    
    @Query("DELETE FROM quiz_completions WHERE userId = :userId")
    suspend fun clearUserQuizCompletions(userId: Int)

    // Course Enrollment operations
    @Query("SELECT * FROM course_enrollments WHERE isSynced = 0")
    suspend fun getUnsyncedEnrollments(): List<CourseEnrollmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: CourseEnrollmentEntity)

    @Query("UPDATE course_enrollments SET isSynced = 1 WHERE courseId = :courseId")
    suspend fun markEnrollmentAsSynced(courseId: Int)

    @Query("DELETE FROM course_enrollments WHERE courseId = :courseId")
    suspend fun deleteEnrollment(courseId: Int)

    // Check if course data is complete (downloaded)
    // New logic: Course is downloaded if:
    // 1. Course detail exists (mandatory)
    // 2. Course is marked as downloaded (isDownloaded = 1)
    // This handles cases where videos/quizzes are not available on server
    @Query("""
        SELECT CASE 
            WHEN EXISTS(SELECT 1 FROM course_details WHERE id = :courseId) 
            AND EXISTS(SELECT 1 FROM courses WHERE id = :courseId AND isDownloaded = 1)
            THEN 1 ELSE 0 END
    """)
    suspend fun isCourseDataComplete(courseId: Int): Boolean

    // Debug method to check what data exists for a course
    @Query("""
        SELECT 
            (SELECT COUNT(*) FROM course_details WHERE id = :courseId) as hasDetail,
            (SELECT COUNT(*) FROM course_videos WHERE courseId = :courseId) as videoCount,
            (SELECT COUNT(*) FROM course_quizzes WHERE courseId = :courseId) as quizCount,
            (SELECT isDownloaded FROM courses WHERE id = :courseId LIMIT 1) as isDownloaded,
            (SELECT isEnrolled FROM courses WHERE id = :courseId LIMIT 1) as isEnrolled
    """)
    suspend fun getCourseDownloadStatus(courseId: Int): CourseDownloadStatus?
} 