package com.application.elevate.ui.counseling

import com.application.elevate.model.Consultant
import com.application.elevate.model.CounselingCategory
import com.application.elevate.model.CounselingPagination

// State holder for Counseling feature
data class CounselingUiState(
    val categories: List<CounselingCategory> = emptyList(),
    val consultants: List<Consultant> = emptyList(),
    val selectedCategory: CounselingCategory? = null,
    val selectedSpecialization: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val pagination: CounselingPagination? = null
)

// State holder untuk detail counselor
data class CounselorDetailUiState(
    val consultant: Consultant? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)