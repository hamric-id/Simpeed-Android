package com.hamric.simpeed

import android.content.Context
import com.hamric.simpeed.feature.login.LoginConfigProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppLoginConfigProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LoginConfigProvider {
    override fun getDefault_web_client_id(): String {
        return context.getString(R.string.default_web_client_id)
    }
}