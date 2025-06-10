package com.application.elevate.data.mapper

import com.application.elevate.model.CVReviewResponse
import com.application.elevate.model.CVReviewData
import com.application.elevate.model.CVReviewItem
import com.application.elevate.data.database.entity.CVReviewEntity
import com.application.elevate.model.AIAnalysis
import com.application.elevate.model.CVScores
import java.util.*
import java.text.SimpleDateFormat

// Convert API Response to Room Entity
fun CVReviewResponse.toEntity(fileName: String, careerField: String): CVReviewEntity {
  return CVReviewEntity(
    id = data.id,
    fileName = fileName,
    careerField = careerField,
    uploadDate = System.currentTimeMillis(),
    overallScore = data.scores.overallScore.toFloat(),
    technicalScore = data.scores.relevantSkill.toFloat(),
    softSkillScore = data.scores.workExperience.toFloat(),
    experienceScore = data.scores.consistency.toFloat(),
    aiAnalysis = data.aiAnalysis.summary,
    suggestions = data.suggestions.joinToString("\n"),
    status = "completed"
  )
}

// Convert Room Entity to CVReviewItem (untuk list)
fun CVReviewEntity.toCVReviewItem(): CVReviewItem {
  val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
  val formattedDate = dateFormat.format(Date(uploadDate))
  
  return CVReviewItem(
    id = id,
    fileName = fileName,
    careerField = careerField,
    overallScore = overallScore.toDouble(),
    relevancyRate = technicalScore.toDouble(), // Map technical to relevancy
    targetedJobRate = softSkillScore.toDouble(), // Map soft skill to targeted job
    createdAt = formattedDate
  )
}

// Convert Room Entity to Display Model (kalau diperlukan)
fun CVReviewEntity.toDisplayModel(): CVReviewDisplayModel {
  return CVReviewDisplayModel(
    id = id,
    fileName = fileName,
    careerField = careerField,
    uploadDate = uploadDate,
    overallScore = overallScore,
    technicalScore = technicalScore,
    softSkillScore = softSkillScore,
    experienceScore = experienceScore,
    aiAnalysis = aiAnalysis,
    suggestions = suggestions.split("\n"),
    status = status
  )
}

// Convert CVReviewItem (dari API) to CVReviewEntity (untuk save ke Room)
fun CVReviewItem.toEntity(): CVReviewEntity {
  val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
  val timestamp = try {
    inputFormat.parse(createdAt)?.time ?: System.currentTimeMillis()
  } catch (e: Exception) {
    System.currentTimeMillis()
  }
  
  return CVReviewEntity(
    id = id,
    fileName = fileName,
    careerField = careerField,
    uploadDate = timestamp,
    overallScore = overallScore.toFloat(),
    technicalScore = relevancyRate.toFloat(),
    softSkillScore = targetedJobRate.toFloat(),
    experienceScore = 0f, // Default value
    aiAnalysis = "", // Akan diisi pas get detail
    suggestions = "",
    status = "completed"
  )
}

// Display model buat UI
data class CVReviewDisplayModel(
  val id: String,
  val fileName: String,
  val careerField: String,
  val uploadDate: Long,
  val overallScore: Float,
  val technicalScore: Float,
  val softSkillScore: Float,
  val experienceScore: Float,
  val aiAnalysis: String,
  val suggestions: List<String>,
  val status: String
)

// Convert Room Entity to CVReviewData for offline detail view
fun CVReviewEntity.toSimpleCVReviewData(): CVReviewData {
  val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
  val formattedDate = dateFormat.format(Date(uploadDate))
  
  return CVReviewData(
    id = id,
    fileName = fileName,
    careerField = careerField,
    fileUrl = filePath ?: "", // Local file path or empty
    fileSize = 0, // Not stored in entity
    scores = CVScores(
      relevancyRate = technicalScore.toDouble(),
      targetedJobRate = softSkillScore.toDouble(),
      overallScore = overallScore.toDouble(),
      relevantSkill = technicalScore.toDouble(),
      workExperience = experienceScore.toDouble(),
      consistency = (technicalScore + softSkillScore) / 2.0, // Estimated
      writingQuality = (overallScore * 0.8).toDouble() // Estimated
    ),
    aiAnalysis = AIAnalysis(
      summary = aiAnalysis.ifEmpty { "Analysis not available offline" },
      strengths = suggestions.split("\n").filter { it.contains("strength", true) },
      weaknesses = suggestions.split("\n").filter { it.contains("improve", true) || it.contains("weak", true) },
      careerFieldFit = "Career field analysis not available offline"
    ),
    suggestions = suggestions.split("\n").filter { it.isNotBlank() },
    createdAt = formattedDate
  )
} 