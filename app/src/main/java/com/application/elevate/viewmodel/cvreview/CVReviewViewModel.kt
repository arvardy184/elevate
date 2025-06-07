package com.application.elevate.viewmodel.cvreview



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CVReviewViewModel : ViewModel() {

    fun submitCV(careerField: String, format: String) {
        // Implement logic for submitting CV to backend
        viewModelScope.launch {
            // Simulate backend call
        }
    }
}
