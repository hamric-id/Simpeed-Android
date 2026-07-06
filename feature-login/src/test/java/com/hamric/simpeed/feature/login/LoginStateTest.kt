package com.hamric.simpeed.feature.login

import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class LoginStateTest {

    @Test
    fun `LoginState should have default values`() {
        val state = LoginState()

        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoggedIn).isFalse()
        assertThat(state.user).isNull()
        assertThat(state.error).isNull()
        assertThat(state.isSignInSuccess).isFalse()
    }

    @Test
    fun `LoginState should be copyable with changes`() {
        val state = LoginState()
        val mockUser = mockk<FirebaseUser>()

        val updatedState = state.copy(
            isLoading = true,
            isLoggedIn = false,
            user = mockUser,
            error = "Test error",
            isSignInSuccess = false
        )

        assertThat(updatedState.isLoading).isTrue()
        assertThat(updatedState.isLoggedIn).isFalse()
        assertThat(updatedState.user).isEqualTo(mockUser)
        assertThat(updatedState.error).isEqualTo("Test error")
        assertThat(updatedState.isSignInSuccess).isFalse()
    }

    @Test
    fun `LoginEffect should be sealed class with correct values`() {
        val showError = LoginEffect.ShowError("Test error")
        val navigate = LoginEffect.NavigateToSpeedometer

        assertThat(showError).isInstanceOf(LoginEffect::class.java)
        assertThat(navigate).isInstanceOf(LoginEffect::class.java)
        assertThat(showError.message).isEqualTo("Test error")
    }

    @Test
    fun `LoginIntent should be sealed class with correct types`() {
        val signIn = LoginIntent.SignInWithGoogle("token")
        val signOut = LoginIntent.SignOut
        val checkAuth = LoginIntent.CheckAuthStatus
        val resetError = LoginIntent.ResetError

        assertThat(signIn).isInstanceOf(LoginIntent::class.java)
        assertThat(signOut).isInstanceOf(LoginIntent::class.java)
        assertThat(checkAuth).isInstanceOf(LoginIntent::class.java)
        assertThat(resetError).isInstanceOf(LoginIntent::class.java)

        assertThat(signIn.idToken).isEqualTo("token")
    }
}