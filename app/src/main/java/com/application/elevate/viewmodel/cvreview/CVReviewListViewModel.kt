package com.application.elevate.viewmodel.cvreview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.CVReviewRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.data.mapper.toCVReviewItem
import com.application.elevate.model.CVReviewItem
import com.application.elevate.model.Pagination
import com.application.elevate.util.NetworkUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CVReviewListViewModel @Inject constructor(
  private val cvReviewRepository: CVReviewRepository,
  private val userRepository: UserRepository,
  private val networkUtil: NetworkUtil
) : ViewModel() {

  private val _uiState = MutableStateFlow(CVReviewListUiState())
  val uiState: StateFlow<CVReviewListUiState> = _uiState.asStateFlow()

  init {
    loadCVReviews()
  }

  fun loadCVReviews(page: Int = 1) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      
      val isOnline = networkUtil.isOnline()
      _uiState.value = _uiState.value.copy(isOffline = !isOnline)
      
      if (isOnline) {
        // Online: Fetch dari API + sync ke Room
        loadFromAPI(page)
      } else {
        // Offline: Load dari Room
        loadFromLocal()
      }
    }
  }
  
  private suspend fun loadFromAPI(page: Int) {
    try {
      val token = userRepository.getAuthToken()
      
      if (token == null) {
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Session expired. Please login again."
        )
        return
      }
      
      cvReviewRepository.getMyCVReviews(token, page)
        .onSuccess { response ->
          Log.d("CVReviewListVM", "Loaded ${response.data.size} CV reviews from API")
          
          val currentList = if (page == 1) emptyList() else _uiState.value.cvReviews
          val newList = currentList + response.data
          
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            cvReviews = newList,
            pagination = response.pagination,
            currentPage = page,
            isOffline = false
          )
        }
        .onFailure { exception ->
          Log.e("CVReviewListVM", "API failed, trying local data", exception)
          // Fallback ke local data kalau API gagal
          loadFromLocal()
        }
    } catch (e: Exception) {
      Log.e("CVReviewListVM", "Error getting token", e)
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        error = "Failed to get authentication token"
      )
    }
  }
  
  private suspend fun loadFromLocal() {
    try {
      val localData = cvReviewRepository.getAllCVReviewsLocal().first()
      val cvReviewItems = localData.map { it.toCVReviewItem() }
      
      Log.d("CVReviewListVM", "Loaded ${cvReviewItems.size} CV reviews from local database")
      
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        cvReviews = cvReviewItems,
        pagination = Pagination(
          page = 1,
          limit = cvReviewItems.size,
          total = cvReviewItems.size,
          totalPages = 1
        ),
        currentPage = 1,
        isOffline = true
      )
    } catch (e: Exception) {
      Log.e("CVReviewListVM", "Failed to load from local database", e)
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        error = "Failed to load CV reviews. Please check your connection.",
        isOffline = true
      )
    }
  }

  fun loadMoreCVReviews() {
    val currentState = _uiState.value
    // Only allow load more when online
    if (!currentState.isLoading && 
        !currentState.isOffline && 
        currentState.currentPage < currentState.pagination.totalPages) {
      loadCVReviews(currentState.currentPage + 1)
    }
  }

  fun refreshCVReviews() {
    loadCVReviews(1)
  }

  fun deleteCVReview(reviewId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      
      if (!networkUtil.isOnline()) {
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Cannot delete while offline. Please check your connection."
        )
        return@launch
      }
      
      try {
        val token = userRepository.getAuthToken()
        
        if (token == null) {
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = "Session expired. Please login again."
          )
          return@launch
        }
        
        cvReviewRepository.deleteCVReview(token, reviewId)
          .onSuccess { response ->
            Log.d("CVReviewListVM", "Deleted CV review successfully: ${response.message}")
            
            // Remove from local list
            val updatedList = _uiState.value.cvReviews.filter { it.id != reviewId }
            
            _uiState.value = _uiState.value.copy(
              isLoading = false,
              cvReviews = updatedList,
              successMessage = "CV review berhasil dihapus!"
            )
          }
          .onFailure { exception ->
            Log.e("CVReviewListVM", "Failed to delete CV review", exception)
            _uiState.value = _uiState.value.copy(
              isLoading = false,
              error = exception.message ?: "Failed to delete CV review"
            )
          }
      } catch (e: Exception) {
        Log.e("CVReviewListVM", "Error deleting CV review", e)
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Failed to delete CV review"
        )
      }
    }
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }

  fun clearSuccessMessage() {
    _uiState.value = _uiState.value.copy(successMessage = null)
  }
}

data class CVReviewListUiState(
  val isLoading: Boolean = false,
  val cvReviews: List<CVReviewItem> = emptyList(),
  val pagination: Pagination = Pagination(
    page = 1,
    limit = 10,
    total = 0,
    totalPages = 0
  ),
  val currentPage: Int = 1,
  val error: String? = null,
  val successMessage: String? = null,
  val isOffline: Boolean = false
) 