package com.application.elevate.ui.home

data class SearchUiState(
    val searchHistory: List<String> = emptyList(),
    val isDialogVisible: Boolean = false
)
