package com.application.elevate.data.di

import com.application.elevate.data.repository.CourseOfflineRepository
import com.application.elevate.data.repository.CourseOfflineRepositoryImpl
import com.application.elevate.data.repository.CategoryOfflineRepository
import com.application.elevate.data.repository.CategoryOfflineRepositoryImpl
import com.application.elevate.data.repository.DataStoreRepository
import com.application.elevate.data.repository.DataStoreRepositoryImpl
import com.application.elevate.data.repository.HomeRepository
import com.application.elevate.data.repository.HomeRepositoryImpl
import com.application.elevate.data.repository.JobMatchingRepository
import com.application.elevate.data.repository.JobMatchingRepositoryImpl
import com.application.elevate.data.repository.JobMatchingOfflineRepository
import com.application.elevate.data.repository.JobMatchingOfflineRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
 
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindCourseOfflineRepository(
        courseOfflineRepositoryImpl: CourseOfflineRepositoryImpl
    ): CourseOfflineRepository

    @Binds
    @Singleton
    abstract fun bindCategoryOfflineRepository(
        categoryOfflineRepositoryImpl: CategoryOfflineRepositoryImpl
    ): CategoryOfflineRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        dataStoreRepositoryImpl: DataStoreRepositoryImpl
    ): DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindJobMatchingRepository(
        jobMatchingRepositoryImpl: JobMatchingRepositoryImpl
    ): JobMatchingRepository

    @Binds
    @Singleton
    abstract fun bindJobMatchingOfflineRepository(
        jobMatchingOfflineRepositoryImpl: JobMatchingOfflineRepositoryImpl
    ): JobMatchingOfflineRepository
} 