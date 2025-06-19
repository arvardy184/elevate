package com.application.elevate.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.application.elevate.data.repository.SearchRepository
import com.application.elevate.model.*
import com.application.elevate.data.database.entity.SearchHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val searchRepository: SearchRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(SearchUiState())
  val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

  private var searchJob: Job? = null
  private var debounceJob: Job? = null

  init {
    loadInitialData()
  }

  private fun loadInitialData() {
    viewModelScope.launch {
      try {
        // Load search history
        searchRepository.getSearchHistory().collect { history ->
          _uiState.value = _uiState.value.copy(
            searchHistory = history,
            isLoading = false
          )
        }
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Failed to load search history"
        )
      }
    }
    
    viewModelScope.launch {
      try {
        // Load popular courses for suggestions
        val popularCourses = searchRepository.getPopularCourses(5)
        _uiState.value = _uiState.value.copy(
          popularCourses = popularCourses
        )
      } catch (e: Exception) {
        // Handle error gracefully - don't show error for popular courses
        _uiState.value = _uiState.value.copy(
          popularCourses = emptyList()
        )
      }
    }
  }

  fun onQueryChanged(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
    
    // Cancel previous debounce
    debounceJob?.cancel()
    
    if (query.isBlank()) {
      clearSearchResults()
      return
    }
    
    // Debounce search for better UX
    debounceJob = viewModelScope.launch {
      delay(300) // Wait 300ms before searching
      if (query.length >= 2) {
        performSearch(query)
        loadSearchSuggestions(query)
      }
    }
  }

  fun performSearch(query: String = _uiState.value.searchQuery) {
    if (query.isBlank()) return
    
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        isLoading = true,
        error = null
      )
      
      try {
        val courses = when (_uiState.value.selectedContentType) {
          "course" -> searchRepository.searchCourses(query)
          "consultant" -> emptyList()
          else -> searchRepository.searchCourses(query)
        }
        
        val consultants = when (_uiState.value.selectedContentType) {
          "consultant" -> searchRepository.searchConsultants(query)
          "course" -> emptyList()
          else -> searchRepository.searchConsultants(query)
        }
        
        _uiState.value = _uiState.value.copy(
          searchResults = SearchResults(
            query = query,
            courses = courses,
            consultants = consultants,
            totalResults = courses.size + consultants.size
          ),
          isLoading = false,
          hasSearched = true
        )
        
        // Track search history
        searchRepository.addSearchHistory(query, _uiState.value.selectedContentType)
        
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
          error = "Search failed: ${e.message}",
          isLoading = false
        )
      }
    }
  }

  fun selectContentType(contentType: String) {
    _uiState.value = _uiState.value.copy(selectedContentType = contentType)
    
    if (_uiState.value.hasSearched) {
      performSearch()
    }
  }

  fun selectCategory(categoryId: String) {
    _uiState.value = _uiState.value.copy(selectedCategory = categoryId)
    
    if (_uiState.value.hasSearched) {
      performSearch()
    }
  }

  private fun loadSearchSuggestions(query: String) {
    viewModelScope.launch {
      try {
        val suggestions = searchRepository.getSearchSuggestions(query)
        _uiState.value = _uiState.value.copy(
          searchSuggestions = suggestions
        )
      } catch (e: Exception) {
        // Ignore suggestion errors
      }
    }
  }

  fun onSearchHistorySelected(searchHistory: SearchHistoryEntity) {
    _uiState.value = _uiState.value.copy(
      searchQuery = searchHistory.searchQuery,
      selectedContentType = searchHistory.searchType
    )
    performSearch(searchHistory.searchQuery)
  }

  fun onSuggestionSelected(suggestion: String) {
    _uiState.value = _uiState.value.copy(searchQuery = suggestion)
    performSearch(suggestion)
  }

  fun clearSearchResults() {
    _uiState.value = _uiState.value.copy(
      searchResults = null,
      hasSearched = false,
      searchSuggestions = emptyList()
    )
  }

  fun clearSearchQuery() {
    _uiState.value = _uiState.value.copy(searchQuery = "")
    clearSearchResults()
  }

  fun clearAllSearchHistory() {
    viewModelScope.launch {
      try {
        searchRepository.clearSearchHistory()
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
          error = "Failed to clear search history"
        )
      }
    }
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }

  override fun onCleared() {
    super.onCleared()
    searchJob?.cancel()
    debounceJob?.cancel()
  }
}

// Simple UI State for Search
data class SearchUiState(
  val searchQuery: String = "",
  val searchResults: SearchResults? = null,
  val searchHistory: List<SearchHistoryEntity> = emptyList(),
  val searchSuggestions: List<String> = emptyList(),
  val popularCourses: List<Course> = emptyList(),
  val selectedContentType: String = "all", // "course", "consultant", "all"
  val selectedCategory: String? = null,
  val isLoading: Boolean = true, // Start with loading state
  val hasSearched: Boolean = false,
  val error: String? = null
)

// Simple Search Results
data class SearchResults(
  val query: String,
  val courses: List<Course> = emptyList(),
  val consultants: List<Consultant> = emptyList(),
  val totalResults: Int = 0
)