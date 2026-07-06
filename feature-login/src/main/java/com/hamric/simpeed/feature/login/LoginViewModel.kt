package com.hamric.simpeed.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamric.simpeed.core.domain.usecase.CheckAuthStatusUseCase
import com.hamric.simpeed.core.domain.usecase.SignInWithGoogleUseCase
import com.hamric.simpeed.core.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val configProvider: LoginConfigProvider,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase
) : ViewModel() {

    fun getDefault_web_client_id(): String = configProvider.getDefault_web_client_id()

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    init {
        checkAuthStatus()
    }

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SignInWithGoogle -> signInWithGoogle(intent.idToken)
            LoginIntent.SignOut -> signOut()
            LoginIntent.CheckAuthStatus -> checkAuthStatus()
            LoginIntent.ResetError -> resetError()
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val user = checkAuthStatusUseCase()
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = user != null,
                        user = user,
                        isSignInSuccess = user != null
                    )
                }
                if (user != null) {
                    _effect.emit(LoginEffect.NavigateToSpeedometer)
                }
            } catch (e: Exception) {
                val errorMessage = "Auth check failed: ${e.message}"
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                _effect.emit(LoginEffect.ShowError(errorMessage))
            }
        }
    }

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = signInWithGoogleUseCase(idToken)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        isSignInSuccess = true
                    )
                }
                _effect.emit(LoginEffect.NavigateToSpeedometer)
            } catch (e: Exception) {
                val errorMessage = "Sign-in failed: ${e.message}"
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                _effect.emit(LoginEffect.ShowError(errorMessage))
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                signOutUseCase()
                _state.update {
                    LoginState()
                }
            } catch (e: Exception) {
                val errorMessage = "Sign out failed: ${e.message}"
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                _effect.emit(LoginEffect.ShowError(errorMessage))
            }
        }
    }

    private fun resetError() {
        _state.update { it.copy(error = null) }
    }
}