package com.application.elevate.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("firstName")
    val firstName: String?,

    @SerializedName("lastName")
    val lastName: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("birthDate")
    val birthDate: String?,

    @SerializedName("profilePicture")
    val profilePicture: String? // String Base64 dari gambar
) 