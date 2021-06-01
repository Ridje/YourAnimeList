package com.kis.youranimelist.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

object CustomOkHTTPClient {

    fun getOkHTTPClient(authType : String, authToken : String) : OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authType, authToken))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
}

class AuthInterceptor(private val authType : String, private val authToken : String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder().header("Authorization", "$authType $authToken")
        val request = builder.build()
        return chain.proceed(request)
    }
}