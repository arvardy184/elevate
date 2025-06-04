package com.application.elevate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.application.elevate.data.repository.AuthRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.UserRequest
import android.util.Log
import com.application.elevate.data.repository.DataStoreRepository
import androidx.datastore.preferences.core.booleanPreferencesKey

@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val repository: AuthRepository?,
    private val userRepository: UserRepository?,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val TAG = "LoginViewModel"
    private val USER_IS_ASSESSMENT_COMPLETED_KEY = booleanPreferencesKey("user_is_assessment_completed")

    open val _uiState = MutableStateFlow(LoginUiState())
    open val uiState: StateFlow<LoginUiState> = _uiState

    // State untuk navigasi
    open val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    open val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    init {
        checkLoginStatus()
    }

    open fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    isSuccess = false,
                    error = "Email dan password tidak boleh kosong"
                ) 
            }
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "Memulai proses login dengan remember me: $rememberMe")
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val request = UserRequest(email = email, password = password)
                repository?.login(request)?.collect { result ->
                    result.onSuccess { response ->
                        Log.d(TAG, "Login berhasil, menyimpan token dan data user")
                        // Simpan token dan set remember me
                        userRepository?.setToken(response.token)
                        userRepository?.setRememberMe(rememberMe)
                        
                        // Simpan data user dan status assessment
                        userRepository?.updateUserSync(response.user)
                        userRepository?.updateUser(response.user)
                        
                        // Update status assessment di DataStore
                        userRepository?.let { repo ->
                            dataStoreRepository.edit { prefs ->
                                prefs[USER_IS_ASSESSMENT_COMPLETED_KEY] = response.user.isAssessmentCompleted
                            }
                        }
                        
                        Log.d(TAG, "Data user setelah login: ${response.user}")
                        Log.d(TAG, "Status assessment dari API: ${response.user.isAssessmentCompleted}")
                        
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                message = response.message
                            ) 
                        }
                        
                        // Navigasi berdasarkan status assessment dari API
                        if (response.user.isAssessmentCompleted) {
                            Log.d(TAG, "User sudah assessment, navigasi ke home")
                            _navigationEvent.value = NavigationEvent.NavigateToHome
                        } else {
                            Log.d(TAG, "User belum assessment, navigasi ke assessment")
                            _navigationEvent.value = NavigationEvent.NavigateToAssessment
                        }
                    }.onFailure { error ->
                        Log.e(TAG, "Login gagal: ${error.message}")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                error = error.message ?: "Terjadi kesalahan saat login"
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saat login: ${e.message}")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = e.message ?: "Terjadi kesalahan saat login"
                    ) 
                }
            }
        }
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Memeriksa status login")
                val isLoggedIn = userRepository?.isLoggedIn() ?: false
                
                if (isLoggedIn) {
                    Log.d(TAG, "User sudah login, navigasi ke halaman yang sesuai")
                    // Cek status assessment dari DataStore
                    val isAssessmentCompleted = userRepository?.getAssessmentStatus() ?: false
                    Log.d(TAG, "Status assessment dari DataStore: $isAssessmentCompleted")
                    
                    if (isAssessmentCompleted) {
                        Log.d(TAG, "User sudah assessment, navigasi ke home")
                        _navigationEvent.value = NavigationEvent.NavigateToHome
                    } else {
                        Log.d(TAG, "User belum assessment, navigasi ke assessment")
                        _navigationEvent.value = NavigationEvent.NavigateToAssessment
                    }
                } else {
                    Log.d(TAG, "User belum login atau token expired")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saat memeriksa status login: ${e.message}")
            }
        }
    }

    open fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    object NavigateToAssessment : NavigationEvent()
} 