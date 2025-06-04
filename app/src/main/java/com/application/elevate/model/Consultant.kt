package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class Consultant(
    val id: Int,
    val userId: Int,
    val specialization: String,
    val bio: String,
    val verified: Boolean,
    val users: CounselorUser,
    @SerializedName("_count")
    val count: SessionCount,
    val averageRating: Double,
    val totalSessions: Int
)

data class CounselorUser(
    val firstName: String,
    val lastName: String,
    val email: String
) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class SessionCount(
    val counselingsession: Int
)

data class ConsultantResponse(
    val success: Boolean,
    val data: List<Consultant>,
    val pagination: CounselingPagination
)

data class ConsultantDetailResponse(
    val success: Boolean,
    val data: Consultant
)

data class CounselingPagination(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val itemsPerPage: Int
)