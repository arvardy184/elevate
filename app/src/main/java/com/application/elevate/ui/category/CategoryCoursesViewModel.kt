package com.application.elevate.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class CategoryCoursesViewModel(private val categoryId: String) : ViewModel() {
    
    protected val _uiState = MutableStateFlow(CategoryCoursesUiState())
    val uiState: StateFlow<CategoryCoursesUiState> = _uiState.asStateFlow()
    
    init {
        // Load courses based on category ID
        loadCoursesByCategory(categoryId)
    }
    
    private fun loadCoursesByCategory(categoryId: String) {
        // Seharusnya mengambil dari API atau data source yang sudah ada
        // Sementara kita gunakan dummy data yang ada
        val courses = ProfileDummyData.dummyCourses
        
        // Sebenarnya harus ada filter berdasarkan category
        // Tapi di model Course belum ada field categoryId
        // Jadi sementara tampilkan saja semua Course untuk demo
        
        val categoryName = when(categoryId) {
            "1" -> "Design"
            "2" -> "Web Development"
            "3" -> "Digital Marketing"
            "4" -> "Mobile Development"
            "5" -> "Product Management"
            "6" -> "Finance & Accounting"
            "7" -> "HR Management"
            "8" -> "Personal Branding"
            else -> "All Courses"
        }
        
        _uiState.update { currentState ->
            currentState.copy(
                categoryName = categoryName,
                courses = courses,
                filteredCourses = courses
            )
        }
    }
    
    fun searchCourses(query: String) {
        val filteredList = if (query.isBlank()) {
            uiState.value.courses
        } else {
            uiState.value.courses.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
        
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredCourses = filteredList
            )
        }
    }
    
    // Fungsi untuk testing dan preview
    fun updateUiState(state: CategoryCoursesUiState) {
        _uiState.value = state
    }
}

data class CategoryCoursesUiState(
    val categoryName: String = "",
    val courses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class CategoryCoursesViewModelFactory(private val categoryId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryCoursesViewModel::class.java)) {
            return CategoryCoursesViewModel(categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 