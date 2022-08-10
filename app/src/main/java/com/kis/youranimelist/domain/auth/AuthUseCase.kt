package com.kis.youranimelist.domain.auth

import com.kis.youranimelist.core.utils.AppPreferences
import com.kis.youranimelist.data.network.AuthInterceptor
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
    private val authInterceptor: AuthInterceptor,
    private val oAuthAPI: Lazy<MyAnimeListOAuthAPI>,
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

    fun isAuthDataValid(): Boolean {
        return authInterceptor.authorizationValid()
    }

    fun onAuthError(errorCode: Int): Boolean {
        if (errorCode == 401 && authInterceptor.refreshToken != null) {
            val requestTokenResult = oAuthAPI.get().refreshToken("refresh_token",
                authInterceptor.refreshToken ?: throw RuntimeException(
                    "refresh token was nulled by another Thread"
                )
            ).execute()
            if (requestTokenResult.isSuccessful) {
                val tokenInfo = requestTokenResult.body()
                    ?: throw RuntimeException("Uknown error during refreshing token operation")
                setAuthData(tokenInfo.accessToken,
                    tokenInfo.refreshToken,
                    tokenInfo.expiresIn,
                    tokenInfo.tokenType)
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
}
