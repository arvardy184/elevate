package com.application.elevate.model

data class AssessmentHistoryResponse(
    val status: String,
    val message: String,
    val data: List<AssessmentHistory>,
    val total: Int
)

data class AssessmentHistory(
    val id: Int,
    val studentStatus: String,
    val majorStudy: String,
    val currentSemester: String,
    val currentField: String,
    val interestedField: String,
    val dreamJob: String,
    val mainGoal: String,
    val createdAt: String
) 