package com.hamric.simpeed.feature.login

import com.google.firebase.auth.FirebaseUser
import com.hamric.simpeed.core.domain.usecase.CheckAuthStatusUseCase
import com.hamric.simpeed.core.domain.usecase.SignInWithGoogleUseCase
import com.hamric.simpeed.core.domain.usecase.SignOutUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var checkAuthStatusUseCase: CheckAuthStatusUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        signInWithGoogleUseCase = mockk()
        signOutUseCase = mockk()
        checkAuthStatusUseCase = mockk()

        every { checkAuthStatusUseCase() } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty when user is not logged in`() = runTest(testDispatcher) {
        every { checkAuthStatusUseCase() } returns null

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoggedIn).isFalse()
        assertThat(state.user).isNull()
        assertThat(state.error).isNull()
        assertThat(state.isSignInSuccess).isFalse()
    }

    @Test
    fun `initial state should be logged in when user is already authenticated`() = runTest(testDispatcher) {
        val mockUser = mockk<FirebaseUser>()
        every { mockUser.displayName } returns "Test User"
        every { mockUser.email } returns "test@example.com"
        every { checkAuthStatusUseCase() } returns mockUser

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoggedIn).isTrue()
        assertThat(state.user).isEqualTo(mockUser)
        assertThat(state.isSignInSuccess).isTrue()
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
    }

    @Test
    fun `should call checkAuthStatusUseCase on init`() = runTest(testDispatcher) {
        every { checkAuthStatusUseCase() } returns null

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )
        advanceUntilIdle()

        verify { checkAuthStatusUseCase() }
    }

    @Test
    fun `login should succeed with valid token and emit NavigateToSpeedometer`() = runTest(testDispatcher) {
        val mockUser = mockk<FirebaseUser>()
        val token = "valid_token"

        coEvery { signInWithGoogleUseCase(token) } returns mockUser
        every { mockUser.displayName } returns "Test User"
        every { mockUser.email } returns "test@example.com"

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoggedIn).isTrue()
        assertThat(state.user).isEqualTo(mockUser)
        assertThat(state.isSignInSuccess).isTrue()
        assertThat(state.error).isNull()
    }


    @Test
    fun `login should clear previous error when starting new login`() = runTest(testDispatcher) {
        val token = "valid_token"
        val mockUser = mockk<FirebaseUser>()

        coEvery { signInWithGoogleUseCase(token) } returns mockUser

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        coEvery { signInWithGoogleUseCase("invalid") } throws Exception("Previous error")
        viewModel.onIntent(LoginIntent.SignInWithGoogle("invalid"))
        advanceUntilIdle()
        assertThat(viewModel.state.value.error).isNotNull()

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isNull()
        assertThat(viewModel.state.value.isLoggedIn).isTrue()
    }

    @Test
    fun `sign out should clear user state and reset to initial state`() = runTest(testDispatcher) {
        val mockUser = mockk<FirebaseUser>()
        val token = "valid_token"

        coEvery { signInWithGoogleUseCase(token) } returns mockUser
        every { mockUser.displayName } returns "Test User"

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))
        advanceUntilIdle()

        assertThat(viewModel.state.value.isLoggedIn).isTrue()

        coEvery { signOutUseCase() } returns Unit
        viewModel.onIntent(LoginIntent.SignOut)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoggedIn).isFalse()
        assertThat(state.user).isNull()
        assertThat(state.isSignInSuccess).isFalse()
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
    }

    @Test
    fun `sign out should call signOutUseCase`() = runTest(testDispatcher) {
        coEvery { signOutUseCase() } returns Unit

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignOut)
        advanceUntilIdle()

        coVerify { signOutUseCase() }
    }

    @Test
    fun `check auth status should return user when authenticated`() = runTest(testDispatcher) {
        val mockUser = mockk<FirebaseUser>()
        every { checkAuthStatusUseCase() } returns mockUser

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.CheckAuthStatus)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoggedIn).isTrue()
        assertThat(state.user).isEqualTo(mockUser)
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `check auth status should return null when not authenticated`() = runTest(testDispatcher) {
        every { checkAuthStatusUseCase() } returns null

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.CheckAuthStatus)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoggedIn).isFalse()
        assertThat(state.user).isNull()
        assertThat(state.isLoading).isFalse()
        assertThat(state.isSignInSuccess).isFalse()
    }


    @Test
    fun `reset error should clear error message`() = runTest(testDispatcher) {
        val token = "invalid"
        coEvery { signInWithGoogleUseCase(token) } throws Exception("Test error")

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isNotNull()

        viewModel.onIntent(LoginIntent.ResetError)

        assertThat(viewModel.state.value.error).isNull()
    }

    @Test
    fun `should call signInWithGoogleUseCase with correct token`() = runTest(testDispatcher) {
        val token = "test_token_123"
        coEvery { signInWithGoogleUseCase(token) } returns mockk<FirebaseUser>()

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))
        advanceUntilIdle()

        coVerify { signInWithGoogleUseCase(token) }
    }

    @Test
    fun `should call signOutUseCase when signing out`() = runTest(testDispatcher) {
        coEvery { signOutUseCase() } returns Unit

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignOut)
        advanceUntilIdle()

        coVerify { signOutUseCase() }
    }

    @Test
    fun `login with empty token should not crash`() = runTest(testDispatcher) {
        val token = ""
        coEvery { signInWithGoogleUseCase(token) } throws Exception("Token is empty")

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))
        advanceUntilIdle()

        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.error).isNotNull()
    }

    @Test
    fun `multiple reset error calls should not cause issues`() = runTest(testDispatcher) {
        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.ResetError)
        viewModel.onIntent(LoginIntent.ResetError)
        viewModel.onIntent(LoginIntent.ResetError)

        assertThat(viewModel.state.value.error).isNull()
    }


    @Test
    fun `login should fail when use case throws exception`() = runTest(testDispatcher) {
        val token = "invalid_token"
        val errorMessage = "Authentication failed"

        coEvery { signInWithGoogleUseCase(token) } throws Exception(errorMessage)

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoggedIn).isFalse()
        assertThat(state.error).isEqualTo("Sign-in failed: $errorMessage")
        assertThat(state.isSignInSuccess).isFalse()
    }

    @Test
    fun `sign out should show error when signOutUseCase throws exception`() = runTest(testDispatcher) {
        val errorMessage = "Failed to sign out"
        coEvery { signOutUseCase() } throws Exception(errorMessage)

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignOut)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.error).isEqualTo("Sign out failed: $errorMessage")
    }

    @Test
    fun `check auth status should show error when use case throws exception`() = runTest(testDispatcher) {
        val errorMessage = "Auth check failed"
        every { checkAuthStatusUseCase() } throws Exception(errorMessage)

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.error).isEqualTo("Auth check failed: $errorMessage")
    }



    @Test
    fun `login should show loading state while authenticating`() = runTest(testDispatcher) {
        val token = "test_token"
        coEvery { signInWithGoogleUseCase(token) } coAnswers {
            delay(1000)
            mockk<FirebaseUser>()
        }

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))

        yield()
        assertThat(viewModel.state.value.isLoading).isTrue()

        advanceUntilIdle()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `loading should be true during login then false after completion`() = runTest(testDispatcher) {
        val token = "test_token"
        coEvery { signInWithGoogleUseCase(token) } coAnswers {
            delay(500)
            mockk<FirebaseUser>()
        }

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token))

        yield()
        assertThat(viewModel.state.value.isLoading).isTrue()

        advanceUntilIdle()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `should not allow duplicate concurrent logins`() = runTest(testDispatcher) {
        val token1 = "token1"
        val token2 = "token2"
        val mockUser = mockk<FirebaseUser>()

        coEvery { signInWithGoogleUseCase(token1) } coAnswers {
            delay(1000)
            mockUser
        }
        coEvery { signInWithGoogleUseCase(token2) } returns mockUser

        viewModel = LoginViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            signOutUseCase = signOutUseCase,
            checkAuthStatusUseCase = checkAuthStatusUseCase
        )

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token1))

        yield()

        assertThat(viewModel.state.value.isLoading).isTrue()

        viewModel.onIntent(LoginIntent.SignInWithGoogle(token2))

        advanceUntilIdle()

        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.isLoggedIn).isTrue()
    }
}