package com.hamric.simpeed.core.domain.usecase


import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

open class SignOutUseCase @Inject constructor() {
    suspend operator fun invoke() {
        Firebase.auth.signOut()
    }
}