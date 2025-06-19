package com.application.elevate.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
import com.application.elevate.data.database.dao.CVReviewDao
import com.application.elevate.data.database.dao.ProfileDao
import com.application.elevate.data.database.dao.AssessmentDao
import com.application.elevate.data.database.dao.CourseDao
import com.application.elevate.data.database.dao.CategoryDao
import com.application.elevate.data.database.dao.JobMatchingDao
import com.application.elevate.data.database.entity.CVReviewEntity
import com.application.elevate.data.database.entity.ProfileEntity
import com.application.elevate.data.database.entity.AssessmentEntity
import com.application.elevate.data.database.entity.CourseEntity
import com.application.elevate.data.database.entity.CourseDetailEntity
import com.application.elevate.data.database.entity.CourseVideoEntity
import com.application.elevate.data.database.entity.CourseQuizEntity
import com.application.elevate.data.database.entity.QuizAnswerEntity
import com.application.elevate.data.database.entity.QuizCompletionEntity
import com.application.elevate.data.database.entity.CourseEnrollmentEntity
import com.application.elevate.data.database.entity.CategoryEntity
import com.application.elevate.data.database.entity.JobMatchingEntity
import com.application.elevate.data.database.entity.JobMatchEntity
import com.application.elevate.data.database.entity.AIAnalysisEntity
import com.application.elevate.data.database.converter.StringListConverter
import com.application.elevate.data.database.converter.JobMatchingConverter

@Database(
  entities = [
    CVReviewEntity::class, 
    ProfileEntity::class, 
    AssessmentEntity::class,
    CourseEntity::class,
    CourseDetailEntity::class,
    CourseVideoEntity::class,
    CourseQuizEntity::class,
    QuizAnswerEntity::class,
    QuizCompletionEntity::class,
    CourseEnrollmentEntity::class,
    CategoryEntity::class,
    JobMatchingEntity::class,
    JobMatchEntity::class,
    AIAnalysisEntity::class
  ],
  version = 10,
  exportSchema = false
)
@TypeConverters(StringListConverter::class, JobMatchingConverter::class)
abstract class AppDatabase : RoomDatabase() {
  
  abstract fun consultantDao(): ConsultantDao
  abstract fun counselingCategoryDao(): CounselingCategoryDao
  abstract fun cvReviewDao(): CVReviewDao
  abstract fun searchHistoryDao(): SearchHistoryDao
  abstract fun courseDao(): CourseDao
  abstract fun profileDao(): ProfileDao
  abstract fun assessmentDao(): AssessmentDao
  abstract fun courseDao(): CourseDao
  abstract fun categoryDao(): CategoryDao
  abstract fun jobMatchingDao(): JobMatchingDao
  
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