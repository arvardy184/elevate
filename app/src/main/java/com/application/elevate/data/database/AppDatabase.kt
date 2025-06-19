package com.application.elevate.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.application.elevate.data.database.entity.*
import com.application.elevate.data.database.dao.*

@Database(
  entities = [
    ConsultantEntity::class,
    CounselingCategoryEntity::class,
    CVReviewEntity::class,
    SearchHistoryEntity::class,
    CourseEntity::class
  ],
  version = 4,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  
  abstract fun consultantDao(): ConsultantDao
  abstract fun counselingCategoryDao(): CounselingCategoryDao
  abstract fun cvReviewDao(): CVReviewDao
  abstract fun searchHistoryDao(): SearchHistoryDao
  abstract fun courseDao(): CourseDao
  
  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null
    
    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "elevate_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}