package com.application.elevate.ui.profile

import android.net.Uri
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeProfileViewModel : ProfileViewModel() {

    private val _fakeUiState = MutableStateFlow(
        ProfileUiState(
            user = ProfileDummyData.currentUser,
            isChangingProfilePicture = false,
            selectedImageUri = null
        )
    )

    override val uiState: StateFlow<ProfileUiState> = _fakeUiState

    override fun setProfileImageUri(uri: Uri) {
        _fakeUiState.value = _fakeUiState.value.copy(
            user = _fakeUiState.value.user.copy(photoUrl = uri.toString())
        )
    }

    override fun showChangeProfilePicture() {
        _fakeUiState.value = _fakeUiState.value.copy(isChangingProfilePicture = true)
    }

    override fun hideChangeProfilePicture() {
        _fakeUiState.value = _fakeUiState.value.copy(isChangingProfilePicture = false)
    }

    override fun updateUser(user: User) {
        _fakeUiState.value = _fakeUiState.value.copy(user = user)
    }
}
