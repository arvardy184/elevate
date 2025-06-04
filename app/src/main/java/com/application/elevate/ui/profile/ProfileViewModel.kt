package com.application.elevate.ui.profile

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.data.repository.AuthRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.HelpCenterItem
import com.application.elevate.model.NotificationSetting
import com.application.elevate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // State untuk navigasi
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

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

            try {
                val user = userRepository.getUser() ?: User(
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
                )

                _uiState.update { it.copy(
                    user = user,
                    isLoading = false,
                    error = null
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load user data"
                ) }
            }
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
            try {
                userRepository.updateUser(updatedUser)
                _uiState.update { it.copy(
                    user = updatedUser,
                    error = null
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "Failed to update user data"
                ) }
            }
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

    fun setProfileImageUri(uri: Uri) {
        val currentUser = _uiState.value.user
        _uiState.update { it.copy(user = currentUser.copy(photoUrl = uri.toString())) }
    }

    fun getUserData(): User {
        return _uiState.value.user
    }

    fun logout() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Memulai proses logout")
                _uiState.update { it.copy(isLoading = true) }

                // Hapus data user dan token
                userRepository.clearUser()

                Log.d(TAG, "Logout berhasil, data user dan token telah dihapus")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        message = "Logout berhasil"
                    )
                }

                // Navigasi ke Login
                _navigationEvent.value = NavigationEvent.NavigateToLogin
            } catch (e: Exception) {
                Log.e(TAG, "Error saat logout: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = "Terjadi kesalahan saat logout: ${e.message}"
                    )
                }
            }
        }
    }

    // Reset navigation event setelah digunakan
    fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}

sealed class NavigationEvent {
    object NavigateToLogin : NavigationEvent()
}