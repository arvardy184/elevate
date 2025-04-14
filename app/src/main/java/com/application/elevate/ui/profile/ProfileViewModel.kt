package com.application.elevate.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.HelpCenterItem
import com.application.elevate.model.NotificationSetting
import com.application.elevate.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadActivities()
        loadNotificationSettings()
        loadHelpCenterItems()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true
            ) }

            // In a real app, this would be a repository call
            val user = ProfileDummyData.currentUser

            _uiState.update { it.copy(
                user = user,
                isLoading = false
            ) }
        }
    }

    private fun loadActivities() {
        viewModelScope.launch {
            val activities = ProfileDummyData.activities
            _uiState.update { it.copy(activities = activities) }
        }
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            val settings = ProfileDummyData.notificationSettings
            _uiState.update { it.copy(notificationSettings = settings) }
        }
    }

    private fun loadHelpCenterItems() {
        viewModelScope.launch {
            val items = ProfileDummyData.helpCenterItems
            _uiState.update { it.copy(helpCenterItems = items) }
        }
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            // In a real app, this would be a repository call to update the user
            _uiState.update { it.copy(user = updatedUser) }
        }
    }

    fun toggleNotificationSetting(settingId: String) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.notificationSettings.map { setting ->
                if (setting.id == settingId) {
                    setting.copy(isEnabled = !setting.isEnabled)
                } else {
                    setting
                }
            }
            _uiState.update { it.copy(notificationSettings = updatedSettings) }
        }
    }

    fun setSelectedTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun showChangeProfilePicture() {
        _uiState.update { it.copy(isChangingProfilePicture = true) }
    }

    fun hideChangeProfilePicture() {
        _uiState.update { it.copy(isChangingProfilePicture = false) }
    }

    fun setSelectedImageUri(uri: String?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun toggleHelpItemExpansion(itemId: String) {
        val updatedItems = _uiState.value.helpCenterItems.map { item ->
            if (item.id == itemId) {
                item.copy(isExpanded = !item.isExpanded)
            } else {
                item
            }
        }
        _uiState.update { it.copy(helpCenterItems = updatedItems) }
    }
}