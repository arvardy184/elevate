package com.application.elevate.viewmodel.home

import androidx.lifecycle.ViewModel
import com.application.elevate.ui.home.SearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchUiState(
        searchHistory = listOf(
            "Social Media Marketing",
            "Front-End Development",
            "UI/UX Design",
            "Finance & Accounting",
            "SEO & Content Marketing",
            "Project Management Fundamental",
            "Flutter Implementation"
        )
    )
    )
    val uiState: StateFlow<SearchUiState> = _uiState

    fun removeHistoryItem(item: String) {
        _uiState.value = _uiState.value.copy(
            searchHistory = _uiState.value.searchHistory.filterNot { it == item }
        )
    }

    fun clearHistory() {
        _uiState.value = _uiState.value.copy(searchHistory = emptyList(), isDialogVisible = false)
    }

    fun showDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isDialogVisible = show)
    }
}
