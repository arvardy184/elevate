package com.application.elevate.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.User
import com.application.elevate.ui.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val TAG = "HomeViewModel"
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    
    init {
        // Muat data user saat pertama kali
        loadUserData()
        checkTutorialStatus()
        
        // Memantau perubahan data user secara real-time
        viewModelScope.launch {
            userRepository.userFlow.collectLatest { user ->
                Log.d(TAG, "Data user berubah: $user")
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser()
                Log.d(TAG, "Data user diperbarui: $user")
                if (user != null) {
                    _uiState.value = _uiState.value.copy(user = user)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saat memperbarui data user: ${e.message}")
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser()
                Log.d(TAG, "Data user dimuat: $user")
                if (user != null) {
                    _uiState.value = _uiState.value.copy(user = user)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saat memuat data user: ${e.message}")
            }
        }
    }

    private fun checkTutorialStatus() {
        viewModelScope.launch {
            try {
                val shouldShow = userRepository.shouldShowTutorial()
                _uiState.value = _uiState.value.copy(showTutorial = shouldShow)
            } catch (e: Exception) {
                Log.e(TAG, "Error saat mengecek status tutorial: ${e.message}")
            }
        }
    }

    fun onTutorialComplete() {
        viewModelScope.launch {
            try {
                userRepository.setTutorialShown()
                _uiState.value = _uiState.value.copy(showTutorial = false)
            } catch (e: Exception) {
                Log.e(TAG, "Error saat menyimpan status tutorial: ${e.message}")
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}

// Class terpisah untuk preview
class PreviewHomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            user = User(
                id = 1,
                firstName = "John",
                lastName = "Doe",
                email = "john@example.com",
                photoUrl = "",
                address = "Sample Address",
                phoneNumber = "+62 123-4567-8900",
                gender = "Male",
                birthDate = "01/01/1990",
                role = "USER",
                isAssessmentCompleted = true
            ),
            showTutorial = false
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState

    fun onTutorialComplete() {
        _uiState.value = _uiState.value.copy(showTutorial = false)
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}