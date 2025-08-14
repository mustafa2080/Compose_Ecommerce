package com.company.npw.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.data.local.preferences.PreferencesManager
import com.company.npw.domain.model.User
import com.company.npw.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Idle)
    val resetPasswordState: StateFlow<ResetPasswordState> = _resetPasswordState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            if (authRepository.isUserLoggedIn) {
                authRepository.getCurrentUser().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val user = resource.data
                            if (user != null) {
                                preferencesManager.setUserId(user.id)
                                preferencesManager.setLoggedIn(true)
                                _authState.value = AuthState.Authenticated(user)
                            } else {
                                _authState.value = AuthState.Unauthenticated
                            }
                        }
                        is Resource.Error -> {
                            _authState.value = AuthState.Unauthenticated
                        }
                        is Resource.Loading -> {
                            _authState.value = AuthState.Loading
                        }
                    }
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginWithEmail(email, password).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        preferencesManager.setUserId(user.id)
                        preferencesManager.setLoggedIn(true)
                        _loginState.value = LoginState.Success(user)
                        _authState.value = AuthState.Authenticated(user)
                    }
                    is Resource.Error -> {
                        _loginState.value = LoginState.Error(resource.message ?: "Login failed")
                    }
                    is Resource.Loading -> {
                        _loginState.value = LoginState.Loading
                    }
                }
            }
        }
    }

    fun registerWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            authRepository.registerWithEmail(email, password, name).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        preferencesManager.setUserId(user.id)
                        preferencesManager.setLoggedIn(true)
                        _registerState.value = RegisterState.Success(user)
                        _authState.value = AuthState.Authenticated(user)
                    }
                    is Resource.Error -> {
                        _registerState.value = RegisterState.Error(resource.message ?: "Registration failed")
                    }
                    is Resource.Loading -> {
                        _registerState.value = RegisterState.Loading
                    }
                }
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        preferencesManager.setUserId(user.id)
                        preferencesManager.setLoggedIn(true)
                        _loginState.value = LoginState.Success(user)
                        _authState.value = AuthState.Authenticated(user)
                    }
                    is Resource.Error -> {
                        _loginState.value = LoginState.Error(resource.message ?: "Google login failed")
                    }
                    is Resource.Loading -> {
                        _loginState.value = LoginState.Loading
                    }
                }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _resetPasswordState.value = ResetPasswordState.Success(resource.data)
                    }
                    is Resource.Error -> {
                        _resetPasswordState.value = ResetPasswordState.Error(resource.message ?: "Reset failed")
                    }
                    is Resource.Loading -> {
                        _resetPasswordState.value = ResetPasswordState.Loading
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        preferencesManager.clearAll()
                        _authState.value = AuthState.Unauthenticated
                        clearStates()
                    }
                    is Resource.Error -> {
                        // Handle logout error if needed
                    }
                    is Resource.Loading -> {
                        // Handle loading if needed
                    }
                }
            }
        }
    }

    fun clearLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun clearRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    fun clearResetPasswordState() {
        _resetPasswordState.value = ResetPasswordState.Idle
    }

    private fun clearStates() {
        _loginState.value = LoginState.Idle
        _registerState.value = RegisterState.Idle
        _resetPasswordState.value = ResetPasswordState.Idle
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    data class Success(val message: String) : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}
