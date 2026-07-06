package com.hamric.simpeed.core.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

open class SignInWithGoogleUseCase @Inject constructor() {
    suspend operator fun invoke(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = Firebase.auth.signInWithCredential(credential).await()
        return authResult.user ?: throw Exception("Authentication failed")
    }
}