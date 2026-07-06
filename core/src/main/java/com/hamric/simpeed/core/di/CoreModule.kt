package com.hamric.simpeed.core.di

import com.hamric.simpeed.core.domain.usecase.CheckAuthStatusUseCase
import com.hamric.simpeed.core.domain.usecase.SignInWithGoogleUseCase
import com.hamric.simpeed.core.domain.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideSignInWithGoogleUseCase(): SignInWithGoogleUseCase {
        return SignInWithGoogleUseCase()
    }

    @Provides
    @Singleton
    fun provideSignOutUseCase(): SignOutUseCase {
        return SignOutUseCase()
    }

    @Provides
    @Singleton
    fun provideCheckAuthStatusUseCase(): CheckAuthStatusUseCase {
        return CheckAuthStatusUseCase()
    }
}