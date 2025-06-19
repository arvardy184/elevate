package com.application.elevate.data.api

import com.application.elevate.model.Course
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseApiService {
    
    @GET("courses")
    suspend fun searchCourses(
        @Query("search") searchQuery: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") categoryId: Int? = null,
        @Query("level") level: String? = null,
        @Query("sort") sortBy: String = "rating"
    ): CourseSearchResponse
    
    @GET("courses")
    suspend fun getAllCourses(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") categoryId: Int? = null
    ): CourseSearchResponse
    
    @GET("courses/popular")
    suspend fun getPopularCourses(
        @Query("limit") limit: Int = 10
    ): CourseSearchResponse
}

data class CourseSearchResponse(
    val courses: List<Course>,
    val pagination: CoursePagination
)

data class CoursePagination(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
) 
import com.application.elevate.model.CourseResponse
import com.application.elevate.model.CourseDetailResponse
import com.application.elevate.model.CategoryResponse

import com.application.elevate.model.CourseVideoResponse
import com.application.elevate.model.CourseQuizResponse
import com.application.elevate.model.QuizSubmitRequest
import com.application.elevate.model.QuizSubmitResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.Body

interface CourseApiService {
    @GET("courses")
    suspend fun getCourses(
        @Header("Authorization") token: String
    ): CourseResponse

    @GET("courses/{id}")
    suspend fun getCourseDetail(
        @Header("Authorization") token: String,
        @Path("id") courseId: Int
    ): CourseDetailResponse
    
    @GET("categories")
    suspend fun getCategories(): CategoryResponse
    
    // getCourseProgress API dihapus karena belum tersedia
    
    @GET("courses/{courseId}/videos")
    suspend fun getCourseVideos(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): CourseVideoResponse
    
    @GET("courses/{courseId}/quizzes")
    suspend fun getCourseQuizzes(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): CourseQuizResponse
    
    @POST("courses/{courseId}/quizzes/{quizId}/submit")
    suspend fun submitQuiz(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int,
        @Path("quizId") quizId: Int,
        @Body request: QuizSubmitRequest
    ): QuizSubmitResponse
    
    @POST("courses/{id}/enroll")
    suspend fun enrollCourse(
        @Header("Authorization") token: String,
        @Path("id") courseId: Int,
        @Body emptyBody: Map<String, String> = emptyMap()
    ): CourseDetailResponse
    
    @GET("courses/videos/proxy/{id}")
    suspend fun getVideoProxy(
        @Header("Authorization") token: String,
        @Path("id") videoId: Int
    ): okhttp3.ResponseBody
}

