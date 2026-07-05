package com.hamric.simpeed.core.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class CheckAuthStatusUseCase @Inject constructor() {
    operator fun invoke(): FirebaseUser? {
        return Firebase.auth.currentUser
    }
}