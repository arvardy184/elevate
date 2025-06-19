package com.application.elevate.model

data class CVReviewResponse(
  val status: String,
  val message: String,
  val data: CVReviewData
)

data class CVReviewListResponse(
  val status: String,
  val data: List<CVReviewItem>,
  val pagination: Pagination
)

data class CVReviewItem(
  val id: String,
  val fileName: String,
  val careerField: String,
  val overallScore: Double,
  val relevancyRate: Double,
  val targetedJobRate: Double,
  val createdAt: String
)

data class Pagination(
  val page: Int,
  val limit: Int,
  val total: Int,
  val totalPages: Int
)

data class CVReviewData(
  val id: String,
  val fileName: String,
  val careerField: String,
  val fileUrl: String,
  val fileSize: Int,
  val scores: CVScores,
  val aiAnalysis: AIAnalysis,
  val suggestions: List<String>,
  val createdAt: String
)

data class CVScores(
  val relevancyRate: Double,
  val targetedJobRate: Double,
  val overallScore: Double,
  val relevantSkill: Double,
  val workExperience: Double,
  val consistency: Double,
  val writingQuality: Double
)

data class AIAnalysis(
  val summary: String,
  val strengths: List<String>,
  val weaknesses: List<String>,
  val careerFieldFit: String
)

data class CVReviewDetailResponse(
  val status: String,
  val message: String,
  val data: CVReviewData
) 