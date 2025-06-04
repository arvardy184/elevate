package com.application.elevate.data.repository

import com.application.elevate.model.Course
import com.application.elevate.model.GrowthHub
import com.application.elevate.model.User
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getCurrentUser(): Flow<User?>
    fun getGrowthHubItems(): List<GrowthHub>
    fun getRecommendedCourses(): List<Course>
    fun getCategories(): List<String>
} 