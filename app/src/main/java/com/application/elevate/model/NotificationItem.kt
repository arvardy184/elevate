package com.application.elevate.model

data class NotificationItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val avatarUrl: String? = null,
    val time: String,
    val dateLabel: String // e.g., "Today", "Yesterday"
)

