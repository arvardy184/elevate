package com.application.elevate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.AuthRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.RegisterRequest
import com.application.elevate.model.UserRequest
import com.application.elevate.ui.auth.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val TAG = "AuthViewModel"
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // State untuk navigasi
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { 
                AuthUiState(isSuccess = false, message = "Email dan password tidak boleh kosong")
            }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, message = null) }
                
                repository.login(UserRequest(email, password)).collect { result ->
                    result.onSuccess { response ->
                        Log.d(TAG, "Login berhasil: $response")
                        if (response.user != null) {
                            Log.d(TAG, "Data user dari API: ${response.user}")
                            Log.d(TAG, "Nama depan: ${response.user.firstName}, Nama belakang: ${response.user.lastName}")
                            Log.d(TAG, "Nama lengkap: ${response.user.fullName}")
                            
                            // Simpan token
                            userRepository.setToken(response.token)
                            userRepository.setRememberMe(rememberMe)
                            
                            // Simpan data user dari response
                            userRepository.updateUser(response.user)
                            
                            _uiState.update { 
                                AuthUiState(
                                    isLoading = false, 
                                    isSuccess = true, 
                                    message = response.message,
                                    token = response.token,
                                    isAssessmentCompleted = response.user.isAssessmentCompleted
                                ) 
                            }
                        } else {
                            _uiState.update { 
                                AuthUiState(
                                    isLoading = false,
                                    isSuccess = false,
                                    message = "Data user tidak valid"
                                )
                            }
                        }
                    }.onFailure { error ->
                        Log.e(TAG, "Login failed", error)
                        _uiState.update { 
                            AuthUiState(isLoading = false, isSuccess = false, message = error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during login", e)
                _uiState.update { 
                    AuthUiState(
                        isLoading = false, 
                        isSuccess = false, 
                        message = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        rePassword: String,
        phoneNumber: String
    ) {
        if (email.isBlank() || password.isBlank() || phoneNumber.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            _uiState.update { AuthUiState(isSuccess = false, message = "Semua field harus diisi") }
            return
        }

        if (password != rePassword) {
            _uiState.update { AuthUiState(isSuccess = false, message = "Password dan konfirmasi password tidak sama") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, message = null) }
                
                repository.register(
                    RegisterRequest(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password,
                        phoneNumber = phoneNumber
                    )
                ).collect { result ->
                    result.onSuccess { response ->
                        Log.d(TAG, "Registrasi berhasil: $response")
                        
                        // Bersihkan data user dan token yang mungkin tersimpan
                        userRepository.clearUser()
                        
                        // Update state dengan pesan sukses
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                message = response.message
                            ) 
                        }
                        
                        // Navigasi ke halaman login setelah registrasi berhasil
                        _navigationEvent.value = NavigationEvent.NavigateToLogin
                    }.onFailure { error ->
                        Log.e(TAG, "Registration failed", error)
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                message = error.message ?: "Registrasi gagal"
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during registration", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        message = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    // Reset state saat berpindah screen
    fun resetState() {
        _uiState.update { AuthUiState() }
    }

    // Reset navigation event setelah digunakan
    fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}

sealed class NavigationEvent {
    object NavigateToLogin : NavigationEvent()
}