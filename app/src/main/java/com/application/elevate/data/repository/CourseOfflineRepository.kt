package com.application.elevate.data.repository

import com.application.elevate.data.database.entity.*
import kotlinx.coroutines.flow.Flow

interface CourseOfflineRepository {
    // Course operations
    fun getAllCourses(): Flow<List<CourseEntity>>
    suspend fun getCourseById(courseId: Int): CourseEntity?
    suspend fun insertCourse(course: CourseEntity)
    suspend fun insertCourses(courses: List<CourseEntity>)
    suspend fun updateCourse(course: CourseEntity)
    suspend fun enrollCourseOffline(courseId: Int)
    suspend fun markCourseAsDownloaded(courseId: Int)
    suspend fun markCourseAsSynced(courseId: Int)
    suspend fun deleteAllCourses()
    
    // Course Detail operations
    suspend fun getCourseDetailById(courseId: Int): CourseDetailEntity?
    suspend fun insertCourseDetail(courseDetail: CourseDetailEntity)
    suspend fun deleteCourseDetail(courseId: Int)
    
    // Course Video operations
    suspend fun getCourseVideos(courseId: Int): List<CourseVideoEntity>
    suspend fun insertCourseVideos(videos: List<CourseVideoEntity>)
    suspend fun deleteCourseVideos(courseId: Int)
    
    // Course Quiz operations
    suspend fun getCourseQuizzes(courseId: Int): List<CourseQuizEntity>
    suspend fun insertCourseQuizzes(quizzes: List<CourseQuizEntity>)
    suspend fun deleteCourseQuizzes(courseId: Int)
    
    // Quiz Answer operations
    suspend fun saveQuizAnswers(userId: Int, courseId: Int, answers: List<QuizAnswerEntity>)
    suspend fun getUnsyncedQuizAnswers(userId: Int, courseId: Int): List<QuizAnswerEntity>
    suspend fun getUnsyncedQuizAnswers(userId: Int): List<QuizAnswerEntity>
    suspend fun markQuizAnswersAsSynced(userId: Int, courseId: Int)
    
    // Quiz Completion operations
    suspend fun saveQuizCompletion(userId: Int, courseId: Int, completion: QuizCompletionEntity)
    suspend fun getQuizCompletion(userId: Int, courseId: Int): QuizCompletionEntity?
    suspend fun getAllQuizCompletions(userId: Int): List<QuizCompletionEntity>
    suspend fun markQuizCompletionAsSynced(userId: Int, courseId: Int)
    
    // Clear user quiz data (untuk logout)
    suspend fun clearUserQuizData(userId: Int)
    
    // Enrollment operations
    suspend fun addPendingEnrollment(courseId: Int)
    suspend fun getUnsyncedEnrollments(): List<CourseEnrollmentEntity>
    suspend fun markEnrollmentAsSynced(courseId: Int)
    
    // Sync operations
    suspend fun getUnsyncedCourses(): List<CourseEntity>
    suspend fun isCourseDataComplete(courseId: Int): Boolean
    
    // Debug method to check course download status
    suspend fun getCourseDownloadStatus(courseId: Int): CourseDownloadStatus?
} 