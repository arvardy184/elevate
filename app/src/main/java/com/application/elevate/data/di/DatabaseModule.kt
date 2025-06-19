package com.application.elevate.data.di

import android.content.Context
import androidx.room.Room
import com.application.elevate.data.database.AppDatabase
import com.application.elevate.data.database.dao.*
import com.application.elevate.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(
      context.applicationContext,
      AppDatabase::class.java,
      "elevate_database"
    )
      .fallbackToDestructiveMigration() // For development
      .build()
  }

  @Provides
  fun provideConsultantDao(database: AppDatabase): ConsultantDao = database.consultantDao()

  @Provides
  fun provideCounselingCategoryDao(database: AppDatabase): CounselingCategoryDao = database.counselingCategoryDao()

  @Provides
  fun provideCVReviewDao(database: AppDatabase): CVReviewDao = database.cvReviewDao()

  @Provides
  fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao = database.searchHistoryDao()

  @Provides
  fun provideCourseDao(database: AppDatabase): CourseDao = database.courseDao()


} 