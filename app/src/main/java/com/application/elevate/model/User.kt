package com.application.elevate.model

data class User(
    val id: Int = 0,
    val email: String = "",
    val role: String = "USER",
    val firstName: String = "",
    val lastName: String = "",
    val photoUrl: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val isAssessmentCompleted: Boolean = false
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
} 