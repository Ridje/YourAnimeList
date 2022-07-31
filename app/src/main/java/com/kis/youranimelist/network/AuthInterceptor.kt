package com.kis.youranimelist.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    private var tokenType: String? = null
        private set
    private var accessToken: String? = null
        private set
    var refreshToken: String? = null
        private set


    fun setAuthorization(
        tokenType: String? = this.tokenType,
        accessToken: String? = this.accessToken,
        refreshToken: String? = this.refreshToken,
    ) {
        this.tokenType = tokenType
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun authorizationValid(): Boolean {
        return (tokenType?.isNotBlank() == true) && (accessToken?.isNotBlank() == true)
    }

    fun clearAuthorization() {
        accessToken = null
        tokenType = null
        refreshToken = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (tokenType?.isNotBlank() == true) {
            val builder =
                chain.request().newBuilder().header("Authorization", "$tokenType $accessToken")
            request = builder.build()
        }

        return chain.proceed(request)
    }
}
