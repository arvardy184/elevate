package com.application.elevate.ui.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false,
    val token: String? = null,
    val isAssessmentCompleted: Boolean = false
)