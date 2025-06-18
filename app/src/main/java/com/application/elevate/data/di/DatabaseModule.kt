package com.application.elevate.data.di

import android.content.Context
import androidx.room.Room
import com.application.elevate.data.database.AppDatabase
import com.application.elevate.data.database.dao.*
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
  fun provideAppDatabase(
    @ApplicationContext context: Context
  ): AppDatabase {
    return Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      AppDatabase.DATABASE_NAME
    )
      .fallbackToDestructiveMigration() // For development only
      .build()
  }

  @Provides
  fun provideCVReviewDao(database: AppDatabase): CVReviewDao {
    return database.cvReviewDao()
  }

  @Provides
  fun provideConsultantDao(database: AppDatabase): ConsultantDao {
    return database.consultantDao()
  }

  @Provides
  fun provideCounselingCategoryDao(database: AppDatabase): CounselingCategoryDao {
    return database.counselingCategoryDao()
  }

  @Provides
  fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
    return database.searchHistoryDao()
  }
} 