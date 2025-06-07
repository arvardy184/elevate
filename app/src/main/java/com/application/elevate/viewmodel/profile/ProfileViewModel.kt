package com.application.elevate.viewmodel.profile

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.data.repository.AuthRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.User
import com.application.elevate.ui.profile.ProfileUiState
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
                // Ambil status assessment dari DataStore terlebih dahulu
                val currentAssessmentStatus = userRepository.getAssessmentStatus()
                Log.d(TAG, "Status assessment dari DataStore: $currentAssessmentStatus")

                profileRepository.getProfile().collect { result ->
                    result.onSuccess { response ->
                        val user = response.user
                        Log.d(TAG, "Data user dari API: $user")
                        Log.d(TAG, "Nama depan: ${user.firstName}, Nama belakang: ${user.lastName}")
                        
                        // Update user dengan mempertahankan status assessment dari DataStore
                        val updatedUser = user.copy(isAssessmentCompleted = currentAssessmentStatus)
                        Log.d(TAG, "User setelah update dengan status assessment: $updatedUser")
                        
                        // Update user di repository lokal
                        userRepository.updateUser(updatedUser)
                        
                        // Update state UI
                        _uiState.update { it.copy(
                            user = updatedUser,
                            isLoading = false,
                            error = null
                        ) }
                    }.onFailure { error ->
                        Log.e(TAG, "Error saat mendapatkan profil: ${error.message}")
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load user data"
                        ) }
                    }
                }
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
            var retryCount = 0
            val maxRetries = 3
            
            while (retryCount < maxRetries) {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    
                    Log.d(TAG, "Mencoba update user: $updatedUser")
                    Log.d(TAG, "Nama depan: ${updatedUser.firstName}, Nama belakang: ${updatedUser.lastName}")
                    
                    profileRepository.updateProfile(updatedUser).collect { result ->
                        result.onSuccess { response ->
                            val user = response.user
                            Log.d(TAG, "Response dari API: $response")
                            Log.d(TAG, "Data user setelah update: $user")
                            
                            // Update user di repository lokal
                            userRepository.updateUser(user)
                            
                            // Update state UI
                            _uiState.update { it.copy(
                                user = user,
                                isLoading = false,
                                error = null
                            ) }
                            return@collect
                        }.onFailure { error ->
                            Log.e(TAG, "Error saat update profil: ${error.message}")
                            if (error.message?.contains("Job was cancelled") == true || 
                                error.message?.contains("Socket closed") == true) {
                                retryCount++
                                if (retryCount < maxRetries) {
                                    Log.d(TAG, "Mencoba update profil lagi (percobaan $retryCount)")
                                    return@collect
                                }
                            }
                            _uiState.update { it.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to update user data"
                            ) }
                        }
                    }
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "Error saat update user: ${e.message}")
                    retryCount++
                    if (retryCount >= maxRetries) {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to update user data"
                        ) }
                    }
                }
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
        Log.d(TAG, "Mengatur URI gambar profil: $uri")
        val currentUser = _uiState.value.user
        if (currentUser != null) {
            val updatedUser = currentUser.copy(
                photoUrl = uri.toString(),
                address = currentUser.getAddressOrDefault(),
                phoneNumber = currentUser.getPhoneNumberOrDefault(),
                gender = currentUser.getGenderOrDefault(),
                birthDate = currentUser.getBirthDateOrDefault()
            )
            _uiState.value = _uiState.value.copy(user = updatedUser)
            Log.d(TAG, "URI gambar profil berhasil diatur")
        } else {
            Log.e(TAG, "Tidak dapat mengatur URI gambar profil: user null")
        }
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