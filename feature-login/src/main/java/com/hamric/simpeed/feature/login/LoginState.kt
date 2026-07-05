package com.hamric.simpeed.feature.login

import com.google.firebase.auth.FirebaseUser

data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isSignInSuccess: Boolean = false
)


sealed class LoginEffect {
    data class ShowError(val message: String) : LoginEffect()
    object NavigateToSpeedometer : LoginEffect()
}

sealed class LoginIntent {
    data class SignInWithGoogle(val idToken: String) : LoginIntent()
    object SignOut : LoginIntent()
    object CheckAuthStatus : LoginIntent()
    object ResetError : LoginIntent()
}