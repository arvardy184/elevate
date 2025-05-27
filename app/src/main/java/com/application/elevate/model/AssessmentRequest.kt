package com.application.elevate.model

data class AssessmentRequest(
    val studentStatus: String,
    val majorStudy: String,
    val currentSemester: String,
    val currentField: String,
    val interestedField: String,
    val dreamJob: String,
    val mainGoal: String
) 