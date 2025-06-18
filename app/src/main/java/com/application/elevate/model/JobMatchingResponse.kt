package com.application.elevate.model

data class JobMatchingResponse(
    val status: String,
    val message: String,
    val data: JobMatchingData
)

data class JobMatchingData(
    val id: String,
    val dreamJob: String,
    val matches: List<JobMatch>,
    val aiAnalysis: AIJobAnalysis,
    val createdAt: String,
    val totalMatches: Int,
    val cvSaved: Boolean
)

data class JobMatch(
    val jobId: String,
    val title: String,
    val company: String,
    val reasons: List<String>,
    val strengths: List<String>,
    val matchScore: Int,
    val skillMatch: Int,
    val locationMatch: Int,
    val missingSkills: List<String>,
    val experienceMatch: Int,
    val salaryPotential: String
)

data class AIJobAnalysis(
    val summary: String,
    val skillGaps: List<String>,
    val careerPath: String,
    val recommendations: List<String>,
    val dreamJobAlignment: String
)

data class JobMatchingHistoryResponse(
    val status: String,
    val message: String,
    val data: List<JobMatchingHistoryItem>,
    val total: Int
)

data class JobMatchingHistoryItem(
    val id: String,
    val userId: Int,
    val cvReviewId: String?,
    val dreamJob: String,
    val matches: List<JobMatch>,
    val aiAnalysis: AIJobAnalysis,
    val createdAt: String,
    val updatedAt: String,
    val cvreview: String?
) 