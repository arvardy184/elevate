package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("firstName")
    val firstName: String? = null,
    
    @SerializedName("lastName")
    val lastName: String? = null,
    
    @SerializedName("profilePicture")
    val photoUrl: String? = null,
    
    @SerializedName("address")
    val address: String? = null,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    
    @SerializedName("gender")
    val gender: String? = null,
    
    @SerializedName("birthDate")
    val birthDate: String? = null,
    
    @SerializedName("isAssessmentCompleted")
    val isAssessmentCompleted: Boolean = false
) {
    companion object {
        private const val BASE_URL = "https://api.elevate.my.id/" // Base URL yang benar
    }

    val fullName: String
        get() = "$firstName $lastName".trim()
        
    // Fungsi helper untuk mendapatkan nilai dengan default
    fun getPhotoUrlOrDefault(): String = photoUrl ?: ""
    fun getAddressOrDefault(): String = address ?: ""
    fun getPhoneNumberOrDefault(): String = phoneNumber ?: ""
    fun getGenderOrDefault(): String = gender ?: ""
    fun getBirthDateOrDefault(): String = birthDate ?: ""

    fun getFullProfilePictureUrl(): String {
        return photoUrl ?: ""
    }
} 