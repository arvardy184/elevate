package com.application.elevate.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.model.Course
import com.application.elevate.model.User
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
        
        // Memantau perubahan data user secara real-time
        viewModelScope.launch {
            userRepository.userFlow.collectLatest { user ->
                Log.d(TAG, "Data user berubah: $user")
                _uiState.value = _uiState.value.copy(user = user)
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

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}