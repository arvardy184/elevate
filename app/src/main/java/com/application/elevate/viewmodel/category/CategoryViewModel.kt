package com.application.elevate.viewmodel.category

import androidx.lifecycle.ViewModel
import com.application.elevate.data.dummy.CategoryData
import com.application.elevate.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategoryViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()
    
    init {
        // Load categories from data source
        _uiState.update { currentState ->
            currentState.copy(
                categories = CategoryData.categories
            )
        }
    }
}

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 