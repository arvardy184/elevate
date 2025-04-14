package com.application.elevate.model

data class NotificationSetting(
    val id: String,
    val title: String,
    val isEnabled: Boolean,
    val category: String // "general", "system", "more"
)