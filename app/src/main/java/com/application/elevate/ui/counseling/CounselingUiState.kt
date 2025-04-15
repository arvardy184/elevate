package com.application.elevate.ui.counseling

import com.application.elevate.model.Consultant
import com.application.elevate.model.PaymentDetail

// State holder for Counseling feature
data class CounselingUiState(
    val selectedCategory: String = "",
    val searchQuery: String = "",
    val consultants: List<Consultant> = emptyList(),
    val selectedConsultant: Consultant? = null,
    val paymentDetail: PaymentDetail? = null,
    val isPinVerified: Boolean = false,
)