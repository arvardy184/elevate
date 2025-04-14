package com.application.elevate.ui.profile

import com.application.elevate.model.*

data class ProfileUiState(
    val user: User = User(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val activities: List<Activity> = emptyList(),
    val notificationSettings: List<NotificationSetting> = emptyList(),
    val helpCenterItems: List<HelpCenterItem> = emptyList(),
    val selectedTab: String = "Certificate", // For YourActivity screen
    val isChangingProfilePicture: Boolean = false,
    val selectedImageUri: String? = null
)