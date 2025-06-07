package com.application.elevate.viewmodel.counseling



import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.data.dummy.ProfileDummyData.consultants
import com.application.elevate.model.CounselingCategory
import com.application.elevate.ui.counseling.CounselingUiState

class CounselingViewModel : ViewModel() {


    private val _selectedCategory = mutableStateOf<CounselingCategory?>(null)
    val selectedCategory: State<CounselingCategory?> = _selectedCategory


    private val _showAll = mutableStateOf(false)
    val showAll: State<Boolean> = _showAll

    // Mutable state yang bisa berubah secara internal
    private val _uiState = MutableStateFlow(
        CounselingUiState(
            categories = ProfileDummyData.categoriesCounseling,
            consultants = ProfileDummyData.consultants
        )
    )

    // StateFlow versi public yang hanya bisa dibaca
    val uiState: StateFlow<CounselingUiState> = _uiState

    fun onCategorySelected(category: CounselingCategory) {
        _selectedCategory.value = category
        _showAll.value = false
        val filtered = consultants.filter { it.categoryId == category.id }
        _uiState.value = _uiState.value.copy(consultants = filtered)
    }

    fun showAllConsultants() {
        _selectedCategory.value = null
        _showAll.value = true
        _uiState.value = _uiState.value.copy(consultants = consultants)
    }

}
