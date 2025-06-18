package com.application.elevate.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.application.elevate.data.database.dao.*
import com.application.elevate.data.database.entity.*

@Database(
  entities = [
    CVReviewEntity::class,
    ConsultantEntity::class,
    CounselingCategoryEntity::class,
    SearchHistoryEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun cvReviewDao(): CVReviewDao
  abstract fun consultantDao(): ConsultantDao
  abstract fun counselingCategoryDao(): CounselingCategoryDao
  abstract fun searchHistoryDao(): SearchHistoryDao
  
  companion object {
    const val DATABASE_NAME = "elevate_database"
  }
}