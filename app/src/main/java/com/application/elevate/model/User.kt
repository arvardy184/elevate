package com.application.elevate.model

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val birthDate: String = ""
) {
    val fullName: String
        get() = "$firstName $lastName"
}