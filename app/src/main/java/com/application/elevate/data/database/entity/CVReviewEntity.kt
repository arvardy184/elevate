package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cv_reviews")
data class CVReviewEntity(
  @PrimaryKey val id: String,
  val fileName: String,
  val careerField: String,
  val uploadDate: Long, // timestamp
  val overallScore: Float,
  val technicalScore: Float,
  val softSkillScore: Float,
  val experienceScore: Float,
  val aiAnalysis: String,
  val suggestions: String,
  val status: String = "completed", // pending, completed, error
  val filePath: String? = null // local file path kalau disimpan
)