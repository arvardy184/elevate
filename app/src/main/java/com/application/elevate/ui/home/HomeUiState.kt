package com.application.elevate.ui.home

import com.application.elevate.model.User

data class HomeUiState(
    val user: User? = null,
    val searchQuery: String = "",
    val showTutorial: Boolean = false
)