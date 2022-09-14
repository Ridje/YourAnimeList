package com.kis.youranimelist.data.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    private var authMode: AuthMode? = null

    fun setAuthorization(
        authMode: AuthMode,
    ) {
        this.authMode = authMode
    }

    fun isAuthTokenRefreshable(): Boolean {
        return (authMode as? AuthMode.UserToken)?.refreshToken != null
    }

    fun refreshToken(): String? {
        return (authMode as? AuthMode.UserToken)?.refreshToken
    }

    fun authorizationValid(): Boolean {
        return authMode?.let { mode ->
            when (mode) {
                is AuthMode.UserToken -> (mode.tokenType?.isNotBlank() == true) && (mode.accessToken?.isNotBlank() == true)
                is AuthMode.AppToken -> mode.token?.isNotBlank() == true
            }
        } ?: false
    }

    fun clearAuthorization() {
        authMode = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        authMode?.let { mode ->
            request = when (mode) {
                is AuthMode.UserToken -> interceptUserAuth(chain, request, mode)
                is AuthMode.AppToken -> interceptAppAuth(chain, request, mode)
            }
        }
        return chain.proceed(request)
    }

    private fun interceptUserAuth(
        chain: Interceptor.Chain,
        request: Request,
        mode: AuthMode.UserToken,
    ): Request {
        return if (mode.tokenType?.isNotBlank() == true && mode.accessToken?.isNotBlank() == true) {
            chain.request().newBuilder()
                .header("Authorization", "${mode.tokenType} ${mode.accessToken}").build()
        } else {
            request
        }
    }

    private fun interceptAppAuth(
        chain: Interceptor.Chain,
        request: Request,
        mode: AuthMode.AppToken,
    ): Request {
        return if (mode.token?.isNotBlank() == true) {
            chain.request().newBuilder().header("X-MAL-CLIENT-ID", "${mode.token}").build()
        } else {
            request
        }
    }
}

sealed class AuthMode {
    data class UserToken(
        val tokenType: String?,
        val accessToken: String?,
        val refreshToken: String?,
    ) : AuthMode()

    data class AppToken(
        val token: String?,
    ) : AuthMode()
}
