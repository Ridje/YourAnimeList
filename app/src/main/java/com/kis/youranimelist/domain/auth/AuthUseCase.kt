package com.kis.youranimelist.domain.auth

import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import com.kis.youranimelist.core.utils.Setting
import com.kis.youranimelist.data.network.AuthInterceptor
import com.kis.youranimelist.data.network.AuthMode
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
    private val appPreferences: AppPreferencesWrapper,
    private val authInterceptor: AuthInterceptor,
) {
    private val errorHappenedFlow = MutableSharedFlow<String>()

    fun observerAuthErrors(): SharedFlow<String> {
        return errorHappenedFlow
    }

    fun onFailedRefreshToken() {
        if (this.isClientAuth()) {
            clearAuthData()
            CoroutineScope(context = (Dispatchers.IO)).launch {
                errorHappenedFlow.emit(NavigationKeys.Route.LOGIN)
            }
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

    fun isClientAuth(): Boolean {
        return !appPreferences.readValue(Setting.UseAppAuth)
    }

    fun onAuthError(errorCode: Int): Boolean {
        if (errorCode == HTTP_UNAUTHORIZED && authInterceptor.isAuthTokenRefreshable()) {
            val requestTokenResult = runBlocking {
                remoteDataSource.get().refreshAccessToken(authInterceptor.refreshToken()
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
        appPreferences.removeSetting(Setting.AccessToken)
        appPreferences.removeSetting(Setting.RefreshToken)
        appPreferences.removeSetting(Setting.ExpiresInToken)
        appPreferences.removeSetting(Setting.TypeToken)

        appPreferences.removeSetting(Setting.UseAppAuth)

        authInterceptor.clearAuthorization()
    }

    fun setAuthData(
        accessToken: String? = null,
        refreshToken: String? = null,
        expiresIn: Int? = null,
        tokenType: String? = null,
    ) {
        clearAuthData()
        accessToken?.let {
            appPreferences.writeValue(Setting.AccessToken, accessToken)
        }
        refreshToken?.let {
            appPreferences.writeValue(Setting.RefreshToken, refreshToken)
        }
        expiresIn?.let {
            appPreferences.writeValue(Setting.ExpiresInToken, expiresIn)
        }
        tokenType?.let {
            appPreferences.writeValue(Setting.TypeToken, tokenType)
        }
        authInterceptor.setAuthorization(
            AuthMode.UserToken(
                accessToken = accessToken,
                refreshToken = refreshToken,
                tokenType = tokenType,
            )
        )
    }

    fun setAuthData(
        clientId: String,
    ) {
        clearAuthData()
        appPreferences.writeValue(Setting.UseAppAuth, true)
        authInterceptor.setAuthorization(
            AuthMode.AppToken(
                token = clientId
            )
        )
    }

    suspend fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
    ): ResultWrapper<TokenResponse> {
        return remoteDataSource.get().getAccessToken(clientID, code, codeVerifier)
    }
}
