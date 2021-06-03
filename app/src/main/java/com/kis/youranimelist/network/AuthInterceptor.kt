package com.kis.youranimelist.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {

    private var authType : String? = null
    private var authToken : String? = null

    fun setAuthorization(authType : String, authToken : String) {
        this.authType = authType
        this.authToken = authToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (authType?.isNotBlank() == true) {
            val builder =
                chain.request().newBuilder().header("Authorization", "$authType $authToken")
            request = builder.build()
        }

        return chain.proceed(request)
    }
}