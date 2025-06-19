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