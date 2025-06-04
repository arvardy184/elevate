package com.application.elevate.model

// data/model/LoginRequest.kt
data class LoginRequest(
    val email: String,
    val password: String
)

// data/model/LoginResponse.kt
data class LoginResponse(
    val token: String,
    val userId: String,
    val name: String
)