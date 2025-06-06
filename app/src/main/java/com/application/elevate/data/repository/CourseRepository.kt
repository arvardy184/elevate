package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.CourseApiService
import com.application.elevate.model.CourseResponse
import com.application.elevate.model.CourseDetailResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val courseApiService: CourseApiService
) {
    private val TAG = "CourseRepository"

    suspend fun getCourses(): Result<CourseResponse> {
        return try {
            Log.d(TAG, "Mengambil daftar course")
            val response = courseApiService.getCourses()
            Log.d(TAG, "Berhasil mengambil daftar course")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil daftar course: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getCourseDetail(token: String, courseId: Int): Result<CourseDetailResponse> {
        return try {
            Log.d(TAG, "Mengambil detail course dengan id: $courseId")
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = courseApiService.getCourseDetail(formattedToken, courseId)
            Log.d(TAG, "Berhasil mengambil detail course")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil detail course: ${e.message}")
            Result.failure(e)
        }
    }
} 