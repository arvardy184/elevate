package com.application.elevate.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.application.elevate.data.database.dao.CVReviewDao
import com.application.elevate.data.database.entity.CVReviewEntity

@Database(
  entities = [CVReviewEntity::class],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun cvReviewDao(): CVReviewDao
  
  companion object {
    const val DATABASE_NAME = "elevate_database"
  }
}