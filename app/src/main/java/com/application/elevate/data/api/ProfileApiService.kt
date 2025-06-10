package com.application.elevate.data.api

import com.application.elevate.model.ProfileResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApiService {
    @GET("user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @Multipart
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("firstName") firstName: okhttp3.RequestBody,
        @Part("lastName") lastName: okhttp3.RequestBody,
        @Part("address") address: okhttp3.RequestBody,
        @Part("phoneNumber") phoneNumber: okhttp3.RequestBody,
        @Part("gender") gender: okhttp3.RequestBody,
        @Part("birthDate") birthDate: okhttp3.RequestBody,
        @Part profilePicture: MultipartBody.Part?
    ): ProfileResponse
}