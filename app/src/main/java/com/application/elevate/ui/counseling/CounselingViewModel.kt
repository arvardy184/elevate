package com.application.elevate.ui.counseling

import androidx.lifecycle.ViewModel
import com.application.elevate.data.CounselingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.application.elevate.model.Consultant

class CounselingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CounselingUiState())
    val uiState: StateFlow<CounselingUiState> = _uiState

    fun selectCategory(cat: String) {
        _uiState.update {
            it.copy(
                selectedCategory = cat,
                consultants = CounselingData.getConsultants(cat)
            )
        }
    }

    fun search(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                consultants = CounselingData.searchConsultants(query)
            )
        }
    }

    fun selectConsultant(c: Consultant) {
        _uiState.update { it.copy(selectedConsultant = c) }
    }

    fun loadPaymentDetail() {
        _uiState.update {
            it.copy(paymentDetail = CounselingData.getPaymentDetail())
        }
    }

    fun verifyPin(pin: String) {
        _uiState.update {
            it.copy(isPinVerified = pin.length == 6)
        }
    }
}
