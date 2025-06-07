package com.application.elevate.data.api

import com.application.elevate.model.CourseResponse
import com.application.elevate.model.CourseDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header

interface CourseApiService {
    @GET("courses")
    suspend fun getCourses(): CourseResponse

    @GET("courses/{id}")
    suspend fun getCourseDetail(
        @Header("Authorization") token: String,
        @Path("id") courseId: Int
    ): CourseDetailResponse
} 