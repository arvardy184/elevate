package com.application.elevate.model

data class User(
    val id: Int = 0,
    val email: String = "",
    val role: String = "USER",
    val firstName: String = "",
    val lastName: String = "",
    val photoUrl: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val gender: String? = null,
    val birthDate: String? = null,
    val isAssessmentCompleted: Boolean = false
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
        
    // Fungsi helper untuk mendapatkan nilai dengan default
    fun getPhotoUrlOrDefault(): String = photoUrl ?: ""
    fun getAddressOrDefault(): String = address ?: ""
    fun getPhoneNumberOrDefault(): String = phoneNumber ?: ""
    fun getGenderOrDefault(): String = gender ?: ""
    fun getBirthDateOrDefault(): String = birthDate ?: ""
} 