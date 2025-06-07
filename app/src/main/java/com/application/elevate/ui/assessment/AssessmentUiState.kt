package com.application.elevate.ui.assessment

import com.application.elevate.model.AssessmentStep

data class AssessmentUiState(
    val studentStatus: String = "",
    val major: String = "",
    val semester: String = "",
    val currentField: String = "",
    val interestedField: String = "",
    val dreamJob: String = "",
    val goal: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isAssessmentCompleted: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
