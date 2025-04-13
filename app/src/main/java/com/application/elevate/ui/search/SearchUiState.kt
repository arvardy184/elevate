package com.application.elevate.ui.search

data class SearchUiState(
    val searchHistory: List<String> = emptyList(),
    val isDialogVisible: Boolean = false
)
