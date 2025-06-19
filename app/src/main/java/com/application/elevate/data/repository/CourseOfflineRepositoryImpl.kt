package com.application.elevate.data.repository

import com.application.elevate.data.database.dao.CourseDao
import com.application.elevate.data.database.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseOfflineRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao
) : CourseOfflineRepository {

    override fun getAllCourses(): Flow<List<CourseEntity>> {
        return courseDao.getAllCourses()
    }

    override suspend fun getCourseById(courseId: Int): CourseEntity? {
        return courseDao.getCourseById(courseId)
    }

    override suspend fun insertCourse(course: CourseEntity) {
        courseDao.insertCourse(course)
    }

    override suspend fun insertCourses(courses: List<CourseEntity>) {
        courseDao.insertCourses(courses)
    }

    override suspend fun updateCourse(course: CourseEntity) {
        courseDao.updateCourse(course)
    }

    override suspend fun enrollCourseOffline(courseId: Int) {
        courseDao.enrollCourse(courseId, isSynced = false)
    }

    override suspend fun markCourseAsDownloaded(courseId: Int) {
        courseDao.markCourseAsDownloaded(courseId)
    }

    override suspend fun markCourseAsSynced(courseId: Int) {
        courseDao.markCourseAsSynced(courseId)
    }

    override suspend fun deleteAllCourses() {
        courseDao.deleteAllCourses()
    }

    override suspend fun getCourseDetailById(courseId: Int): CourseDetailEntity? {
        return courseDao.getCourseDetailById(courseId)
    }

    override suspend fun insertCourseDetail(courseDetail: CourseDetailEntity) {
        courseDao.insertCourseDetail(courseDetail)
    }

    override suspend fun deleteCourseDetail(courseId: Int) {
        courseDao.deleteCourseDetail(courseId)
    }

    override suspend fun getCourseVideos(courseId: Int): List<CourseVideoEntity> {
        return courseDao.getCourseVideos(courseId)
    }

    override suspend fun insertCourseVideos(videos: List<CourseVideoEntity>) {
        courseDao.insertCourseVideos(videos)
    }

    override suspend fun deleteCourseVideos(courseId: Int) {
        courseDao.deleteCourseVideos(courseId)
    }

    override suspend fun getCourseQuizzes(courseId: Int): List<CourseQuizEntity> {
        return courseDao.getCourseQuizzes(courseId)
    }

    override suspend fun insertCourseQuizzes(quizzes: List<CourseQuizEntity>) {
        courseDao.insertCourseQuizzes(quizzes)
    }

    override suspend fun deleteCourseQuizzes(courseId: Int) {
        courseDao.deleteCourseQuizzes(courseId)
    }

    override suspend fun saveQuizAnswers(userId: Int, courseId: Int, answers: List<QuizAnswerEntity>) {
        courseDao.insertQuizAnswers(answers)
    }

    override suspend fun getUnsyncedQuizAnswers(userId: Int, courseId: Int): List<QuizAnswerEntity> {
        return courseDao.getUnsyncedQuizAnswers(userId, courseId)
    }

    override suspend fun getUnsyncedQuizAnswers(userId: Int): List<QuizAnswerEntity> {
        return courseDao.getUnsyncedQuizAnswers(userId)
    }

    override suspend fun markQuizAnswersAsSynced(userId: Int, courseId: Int) {
        courseDao.markQuizAnswersAsSynced(userId, courseId)
    }
    
    override suspend fun saveQuizCompletion(userId: Int, courseId: Int, completion: QuizCompletionEntity) {
        courseDao.insertQuizCompletion(completion)
    }
    
    override suspend fun getQuizCompletion(userId: Int, courseId: Int): QuizCompletionEntity? {
        return courseDao.getQuizCompletion(userId, courseId)
    }
    
    override suspend fun getAllQuizCompletions(userId: Int): List<QuizCompletionEntity> {
        return courseDao.getAllQuizCompletions(userId)
    }
    
    override suspend fun markQuizCompletionAsSynced(userId: Int, courseId: Int) {
        courseDao.markQuizCompletionAsSynced(userId, courseId)
    }

    override suspend fun addPendingEnrollment(courseId: Int) {
        val enrollment = CourseEnrollmentEntity(courseId = courseId, isSynced = false)
        courseDao.insertEnrollment(enrollment)
    }

    override suspend fun getUnsyncedEnrollments(): List<CourseEnrollmentEntity> {
        return courseDao.getUnsyncedEnrollments()
    }

    override suspend fun markEnrollmentAsSynced(courseId: Int) {
        courseDao.markEnrollmentAsSynced(courseId)
    }

    override suspend fun getUnsyncedCourses(): List<CourseEntity> {
        return courseDao.getUnsyncedCourses()
    }

    override suspend fun isCourseDataComplete(courseId: Int): Boolean {
        return courseDao.isCourseDataComplete(courseId)
    }

    override suspend fun getCourseDownloadStatus(courseId: Int): CourseDownloadStatus? {
        return courseDao.getCourseDownloadStatus(courseId)
    }

    override suspend fun clearUserQuizData(userId: Int) {
        courseDao.clearUserQuizAnswers(userId)
        courseDao.clearUserQuizCompletions(userId)
    }
} 