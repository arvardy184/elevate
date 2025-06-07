package com.application.elevate.data.repository

import com.application.elevate.model.Consultant
import com.application.elevate.model.ConsultantResponse
import com.application.elevate.model.ConsultantDetailResponse

interface CounselingRepository {
  suspend fun getCounselors(
    page: Int = 1,
    limit: Int = 10,
    specialization: String? = null
  ): Result<ConsultantResponse>
  
  suspend fun getCounselorDetail(counselorId: Int): Result<ConsultantDetailResponse>
} 