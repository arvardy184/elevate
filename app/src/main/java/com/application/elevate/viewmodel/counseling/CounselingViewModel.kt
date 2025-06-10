package com.application.elevate.viewmodel.counseling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.application.elevate.data.repository.CounselingRepository
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.CounselingCategory
import com.application.elevate.ui.counseling.CounselingUiState
import com.application.elevate.ui.counseling.CounselorDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CounselingViewModel @Inject constructor(
  private val counselingRepository: CounselingRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(
    CounselingUiState(
      categories = ProfileDummyData.categoriesCounseling
    )
  )
  val uiState: StateFlow<CounselingUiState> = _uiState.asStateFlow()

  private val _detailUiState = MutableStateFlow(CounselorDetailUiState())
  val detailUiState: StateFlow<CounselorDetailUiState> = _detailUiState.asStateFlow()

  init {
    loadCounselors()
  }

  fun loadCounselors(
    page: Int = 1,
    specialization: String? = null
  ) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        isLoading = true,
        error = null
      )

      counselingRepository.getCounselors(
        page = page,
        specialization = specialization
      ).fold(
        onSuccess = { response ->
          _uiState.value = _uiState.value.copy(
            consultants = response.data,
            pagination = response.pagination,
            selectedSpecialization = specialization,
            isLoading = false
          )
        },
        onFailure = { exception ->
          _uiState.value = _uiState.value.copy(
            error = exception.message ?: "Failed to load counselors",
            isLoading = false
          )
        }
      )
    }
  }

  fun onCategorySelected(category: CounselingCategory) {
    _uiState.value = _uiState.value.copy(
      selectedCategory = category
    )
    
    // Filter berdasarkan specialization yang sesuai dengan category
    val specialization = when (category.id) {
      "1" -> "career-counseling"
      "2" -> "clinical-psychology"
      "3" -> "relationship-therapy"
      "4" -> "psychiatry"
      else -> null
    }
    
    loadCounselors(specialization = specialization)
  }

  fun showAllConsultants() {
    _uiState.value = _uiState.value.copy(
      selectedCategory = null,
      selectedSpecialization = null
    )
    loadCounselors()
  }

  fun loadCounselorDetail(counselorId: Int) {
    viewModelScope.launch {
      _detailUiState.value = _detailUiState.value.copy(
        isLoading = true,
        error = null
      )

      counselingRepository.getCounselorDetail(counselorId).fold(
        onSuccess = { response ->
          _detailUiState.value = _detailUiState.value.copy(
            consultant = response.data,
            isLoading = false
          )
        },
        onFailure = { exception ->
          _detailUiState.value = _detailUiState.value.copy(
            error = exception.message ?: "Failed to load counselor detail",
            isLoading = false
          )
        }
      )
    }
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
    _detailUiState.value = _detailUiState.value.copy(error = null)
  }
}
