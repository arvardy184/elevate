package com.application.elevate.ui.profile

import com.application.elevate.model.*

data class ProfileUiState(
    val user: User = User(
        id = 0,
        firstName = "Guest",
        lastName = "User",
        email = "guest@example.com",
        photoUrl = "",
        address = "Default Address",
        phoneNumber = "+62 000-0000-0000",
        gender = "Unspecified",
        birthDate = "01/01/2000",
        role = "user",
        isAssessmentCompleted = false
    ),
    val activities: List<Activity> = emptyList(),
    val notificationSettings: List<NotificationSetting> = emptyList(),
    val helpCenterItems: List<HelpCenterItem> = emptyList(),
    val selectedTab: String = "Profile",
    val isChangingProfilePicture: Boolean = false,
    val selectedImageUri: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val message: String? = null
)