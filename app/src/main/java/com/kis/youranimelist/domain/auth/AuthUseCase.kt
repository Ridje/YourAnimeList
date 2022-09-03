package com.kis.youranimelist.domain.auth

import com.kis.youranimelist.core.utils.AppPreferences
import com.kis.youranimelist.data.network.AuthInterceptor
import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.http.HTTP_UNAUTHORIZED

class AuthUseCase(
    private val remoteDataSource: Lazy<RemoteDataSource>,
    private val appPreferences: AppPreferences,
    private val authInterceptor: AuthInterceptor,
) {
    private val errorHappenedFlow = MutableSharedFlow<String>()

    fun observerAuthErrors(): SharedFlow<String> {
        return errorHappenedFlow
    }

    fun onFailedRefreshToken() {
        clearAuthData()
        CoroutineScope(context = (Dispatchers.IO)).launch {
            errorHappenedFlow.emit(NavigationKeys.Route.LOGIN)
        }
    }

    fun onLogoutFinished() {
        CoroutineScope(context = (Dispatchers.IO)).launch {
            errorHappenedFlow.emit(NavigationKeys.Route.LOGIN)
        }
    }

    fun isAuthDataValid(): Boolean {
        return authInterceptor.authorizationValid()
    }

    fun onAuthError(errorCode: Int): Boolean {
        if (errorCode == HTTP_UNAUTHORIZED && authInterceptor.refreshToken != null) {
            val requestTokenResult = runBlocking {
                remoteDataSource.get().refreshAccessToken(authInterceptor.refreshToken
                    ?: throw NullPointerException("Refresh token was nullified by another Thread")
                )
            }
            if (requestTokenResult is ResultWrapper.Success) {
                setAuthData(requestTokenResult.data.accessToken,
                    requestTokenResult.data.refreshToken,
                    requestTokenResult.data.expiresIn,
                    requestTokenResult.data.tokenType)
                return true
            }
        }

        return false
    }

    fun clearAuthData() {
        appPreferences.removeSetting(AppPreferences.ACCESS_TOKEN_SETTING_KEY)
        appPreferences.removeSetting(AppPreferences.REFRESH_TOKEN_SETTING_KEY)
        appPreferences.removeSetting(AppPreferences.EXPIRES_IN_TOKEN_SETTING_KEY)
        appPreferences.removeSetting(AppPreferences.TYPE_TOKEN_SETTING_KEY)

        authInterceptor.clearAuthorization()
    }

    fun setAuthData(
        accessToken: String? = null,
        refreshToken: String? = null,
        expiresIn: Int? = null,
        tokenType: String? = null,
    ) {
        accessToken?.let {
            appPreferences.writeString(AppPreferences.ACCESS_TOKEN_SETTING_KEY, accessToken)
            authInterceptor.setAuthorization(accessToken = accessToken)
        }
        refreshToken?.let {
            appPreferences.writeString(AppPreferences.REFRESH_TOKEN_SETTING_KEY, refreshToken)
            authInterceptor.setAuthorization(refreshToken = refreshToken)
        }
        expiresIn?.let {
            appPreferences.writeInt(AppPreferences.EXPIRES_IN_TOKEN_SETTING_KEY, expiresIn)
        }
        tokenType?.let {
            appPreferences.writeString(AppPreferences.TYPE_TOKEN_SETTING_KEY, tokenType)
            authInterceptor.setAuthorization(tokenType = tokenType)
        }
    }

    suspend fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
    ): ResultWrapper<TokenResponse> {
        return remoteDataSource.get().getAccessToken(clientID, code, codeVerifier)
    }
}
