package com.hamric.simpeed.di

import com.hamric.simpeed.AppLoginConfigProvider
import com.hamric.simpeed.feature.login.LoginConfigProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindLoginConfigProvider(
        provider: AppLoginConfigProvider
    ): LoginConfigProvider
}