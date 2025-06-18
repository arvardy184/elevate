package com.application.elevate.viewmodel.counseling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.application.elevate.data.repository.CounselingRepository
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.CounselingCategory
import com.application.elevate.model.ConsultantResponse
import com.application.elevate.ui.counseling.CounselingUiState
import com.application.elevate.ui.counseling.CounselorDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.net.ConnectException
import java.io.IOException

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
    // Cache categories on init
    cacheCategories()
  }
  
  private fun cacheCategories() {
    viewModelScope.launch {
      counselingRepository.cacheCategories(ProfileDummyData.categoriesCounseling)
    }
  }

  fun loadCounselors(
    page: Int = 1,
    specialization: String? = null
  ) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        isLoading = true,
        error = null,
        isOffline = false,
        isFromCache = false
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
            isLoading = false,
            lastRefresh = System.currentTimeMillis(),
            isFromCache = isDataFromCache(response)
          )
        },
        onFailure = { exception ->
          val isNetworkError = exception is ConnectException || exception is IOException
          
          if (isNetworkError) {
            // Try to load from cache when offline
            loadCachedConsultants(specialization)
          } else {
            _uiState.value = _uiState.value.copy(
              error = exception.message ?: "Failed to load counselors",
              isLoading = false
            )
          }
        }
      )
    }
  }
  
  private fun loadCachedConsultants(specialization: String?) {
    viewModelScope.launch {
      try {
        val cachedFlow = if (specialization != null) {
          counselingRepository.searchConsultants(specialization)
        } else {
          counselingRepository.getCachedConsultants()
        }
        
        cachedFlow.first().let { cachedConsultants ->
          if (cachedConsultants.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
              consultants = cachedConsultants,
              isLoading = false,
              isOffline = true,
              isFromCache = true,
              error = null
            )
          } else {
            _uiState.value = _uiState.value.copy(
              error = "No internet connection and no cached data available",
              isLoading = false,
              isOffline = true
            )
          }
        }
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
          error = "Failed to load cached data",
          isLoading = false,
          isOffline = true
        )
      }
    }
  }
  
  private fun isDataFromCache(response: ConsultantResponse): Boolean {
    // Simple heuristic: if pagination shows only 1 page with cached data count, likely from cache
    return response.pagination?.let { 
      it.totalPages == 1 && it.currentPage == 1 
    } ?: false
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
