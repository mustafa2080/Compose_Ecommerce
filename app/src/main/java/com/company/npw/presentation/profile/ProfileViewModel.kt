package com.company.npw.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.User
import com.company.npw.domain.repository.AuthRepository
import com.company.npw.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _uiEvent = MutableStateFlow<ProfileUiEvent?>(null)
    val uiEvent: StateFlow<ProfileUiEvent?> = _uiEvent.asStateFlow()

    init {
        loadUserProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadUserProfile()
            is ProfileEvent.UpdateProfile -> updateProfile(event.user)
            is ProfileEvent.Logout -> logout()
            is ProfileEvent.NavigateToEditProfile -> navigateToEditProfile()
            is ProfileEvent.NavigateToOrderHistory -> navigateToOrderHistory()
            is ProfileEvent.NavigateToAddresses -> navigateToAddresses()
            is ProfileEvent.NavigateToSettings -> navigateToSettings()
            is ProfileEvent.ClearUiEvent -> clearUiEvent()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { userResource ->
                when (userResource) {
                    is Resource.Loading -> {
                        _profileState.value = _profileState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        val user = userResource.data
                        if (user != null) {
                            _profileState.value = _profileState.value.copy(
                                user = user,
                                isLoading = false,
                                error = null
                            )
                        } else {
                            _profileState.value = _profileState.value.copy(
                                isLoading = false,
                                error = "User not found"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _profileState.value = _profileState.value.copy(
                            isLoading = false,
                            error = userResource.message
                        )
                    }
                }
            }
        }
    }

    private fun updateProfile(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _profileState.value = _profileState.value.copy(isUpdating = true)
                    }
                    is Resource.Success -> {
                        _profileState.value = _profileState.value.copy(
                            user = user,
                            isUpdating = false
                        )
                        _uiEvent.value = ProfileUiEvent.ShowMessage("Profile updated successfully")
                    }
                    is Resource.Error -> {
                        _profileState.value = _profileState.value.copy(isUpdating = false)
                        _uiEvent.value = ProfileUiEvent.ShowError(result.message ?: "Failed to update profile")
                    }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _profileState.value = _profileState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _profileState.value = _profileState.value.copy(isLoading = false)
                        _uiEvent.value = ProfileUiEvent.NavigateToLogin
                    }
                    is Resource.Error -> {
                        _profileState.value = _profileState.value.copy(isLoading = false)
                        _uiEvent.value = ProfileUiEvent.ShowError(result.message ?: "Failed to logout")
                    }
                }
            }
        }
    }

    private fun navigateToEditProfile() {
        _uiEvent.value = ProfileUiEvent.NavigateToEditProfile
    }

    private fun navigateToOrderHistory() {
        _uiEvent.value = ProfileUiEvent.NavigateToOrderHistory
    }

    private fun navigateToAddresses() {
        _uiEvent.value = ProfileUiEvent.NavigateToAddresses
    }

    private fun navigateToSettings() {
        _uiEvent.value = ProfileUiEvent.NavigateToSettings
    }

    private fun clearUiEvent() {
        _uiEvent.value = null
    }
}

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val error: String? = null
)

sealed class ProfileEvent {
    object LoadProfile : ProfileEvent()
    data class UpdateProfile(val user: User) : ProfileEvent()
    object Logout : ProfileEvent()
    object NavigateToEditProfile : ProfileEvent()
    object NavigateToOrderHistory : ProfileEvent()
    object NavigateToAddresses : ProfileEvent()
    object NavigateToSettings : ProfileEvent()
    object ClearUiEvent : ProfileEvent()
}

sealed class ProfileUiEvent {
    data class ShowMessage(val message: String) : ProfileUiEvent()
    data class ShowError(val message: String) : ProfileUiEvent()
    object NavigateToLogin : ProfileUiEvent()
    object NavigateToEditProfile : ProfileUiEvent()
    object NavigateToOrderHistory : ProfileUiEvent()
    object NavigateToAddresses : ProfileUiEvent()
    object NavigateToSettings : ProfileUiEvent()
}
