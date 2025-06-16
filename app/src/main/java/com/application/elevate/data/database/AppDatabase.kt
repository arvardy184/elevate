package com.application.elevate.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.application.elevate.data.database.dao.CVReviewDao
import com.application.elevate.data.database.dao.ProfileDao
import com.application.elevate.data.database.dao.AssessmentDao
import com.application.elevate.data.database.entity.CVReviewEntity
import com.application.elevate.data.database.entity.ProfileEntity
import com.application.elevate.data.database.entity.AssessmentEntity

@Database(
  entities = [CVReviewEntity::class, ProfileEntity::class, AssessmentEntity::class],
  version = 4,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun cvReviewDao(): CVReviewDao
  abstract fun profileDao(): ProfileDao
  abstract fun assessmentDao(): AssessmentDao
  
  companion object {
    const val DATABASE_NAME = "elevate_database"
  }
}