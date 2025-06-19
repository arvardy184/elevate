package com.application.elevate.data.repository

import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.Course
import com.application.elevate.model.GrowthHub
import com.application.elevate.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository
) : HomeRepository {
    
    override fun getCurrentUser(): Flow<User?> = userRepository.userFlow
    
    override fun getGrowthHubItems(): List<GrowthHub> {
        return ProfileDummyData.growthHubItems.take(3)
    }
    
    override fun getRecommendedCourses(): List<Course> {
        return ProfileDummyData.dummyCourses
            .filter { it.progressPercent in 0..100 }
            .take(4)
    }

    override fun getCategories(): List<String> {
        return ProfileDummyData.categories
    }
} 