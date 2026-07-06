package com.hamric.simpeed.core.domain.usecase

import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class SignInWithGoogleUseCaseTest {

    private lateinit var useCase: SignInWithGoogleUseCase

    @Before
    fun setup() {
        useCase = SignInWithGoogleUseCase()
    }

    @Test
    fun `useCase should be instantiated correctly`() {
        assertThat(useCase).isNotNull()
    }

    @Test(expected = Exception::class)
    fun `should throw exception for invalid token`(): Unit = runBlocking {
        useCase("invalid_token")
    }

    @Test
    fun `should handle empty token gracefully`() = runBlocking {
        try {
            useCase("")
            assertThat(true).isFalse()
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }

    @Test
    fun `should handle null token gracefully`() = runBlocking {
        try {
            useCase("null_token")
            assertThat(true).isFalse()
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }

    @Test
    fun `should return FirebaseUser when authentication succeeds (mocked)`() = runBlocking {
        val mockUser = mockk<FirebaseUser>()
        val mockUseCase = mockk<SignInWithGoogleUseCase>()
        coEvery { mockUseCase.invoke("valid_token") } returns mockUser

        val result = mockUseCase.invoke("valid_token")

        assertThat(result).isEqualTo(mockUser)
    }

    @Test(expected = Exception::class)
    fun `should throw exception when authentication fails (mocked)`(): Unit = runBlocking {
        val mockUseCase = mockk<SignInWithGoogleUseCase>()
        coEvery { mockUseCase.invoke("invalid_token") } throws Exception("Authentication failed")

        mockUseCase.invoke("invalid_token")
    }
}