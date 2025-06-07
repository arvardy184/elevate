package com.application.elevate.viewmodel.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Initial)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                // Cek apakah ini first launch
                val isFirstLaunch = userRepository.isFirstLaunch()
                
                if (isFirstLaunch) {
                    // Jika first launch, tampilkan onboarding
                    _uiState.value = SplashUiState.ShowOnboarding
                    // Set first launch menjadi false
                    userRepository.setFirstLaunch(false)
                } else {
                    // Jika bukan first launch, cek status login
                    if (userRepository.isLoggedIn()) {
                        userRepository.getUser()?.let { user ->
                            _uiState.value = SplashUiState.LoggedIn(user)
                        } ?: run {
                            _uiState.value = SplashUiState.NotLoggedIn
                        }
                    } else {
                        _uiState.value = SplashUiState.NotLoggedIn
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SplashUiState.NotLoggedIn
            }
        }
    }
}

sealed class SplashUiState {
    object Initial : SplashUiState()
    object ShowOnboarding : SplashUiState()
    data class LoggedIn(val user: User) : SplashUiState()
    object NotLoggedIn : SplashUiState()
} 