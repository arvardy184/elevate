package com.application.elevate.ui.counseling

import com.application.elevate.model.Consultant
import com.application.elevate.model.CounselingCategory
import com.application.elevate.model.PaymentDetail

// State holder for Counseling feature
data class CounselingUiState(
    val categories: List<CounselingCategory> = emptyList(),
    val consultants: List<Consultant> = emptyList()
)