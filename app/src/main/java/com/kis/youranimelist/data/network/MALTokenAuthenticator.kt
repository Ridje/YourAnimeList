package com.kis.youranimelist.data.network

import com.kis.youranimelist.domain.auth.AuthUseCase
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class MALTokenAuthenticator @Inject constructor(
    private val authUseCase: AuthUseCase,
) : okhttp3.Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val retryRequest = authUseCase.onAuthError(response.code)
        return if (retryRequest) {
            response.request.newBuilder().build()
        } else {
            authUseCase.onFailedRefreshToken()
            null
        }
    }
}
