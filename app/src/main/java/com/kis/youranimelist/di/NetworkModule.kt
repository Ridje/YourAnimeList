package com.kis.youranimelist.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kis.youranimelist.domain.auth.AuthUseCase
import com.kis.youranimelist.data.network.AuthInterceptor
import com.kis.youranimelist.data.network.MALTokenAuthenticator
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.core.utils.AppPreferences
import com.kis.youranimelist.core.utils.Urls
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@DisableInstallInCheck
object NetworkModule {

    @Singleton
    @Provides
    fun provideMALTokenAuthenticator(
        authUseCase: AuthUseCase,
    ): MALTokenAuthenticator {
        return MALTokenAuthenticator(authUseCase = authUseCase)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun provideConverterFactory(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        return Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }.asConverterFactory(contentType)
    }

    @Singleton
    @Provides
    fun provideOkHTTPClient(
        authInterceptor: AuthInterceptor,
        malTokenAuthenticator: MALTokenAuthenticator,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(malTokenAuthenticator)
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(appPreferences: AppPreferences): AuthInterceptor {
        return AuthInterceptor().apply {
            setAuthorization(
                appPreferences.readString(AppPreferences.TYPE_TOKEN_SETTING_KEY),
                appPreferences.readString(AppPreferences.ACCESS_TOKEN_SETTING_KEY),
                appPreferences.readString(AppPreferences.REFRESH_TOKEN_SETTING_KEY)
            )
        }
    }


    @Singleton
    @Provides
    fun provideMALOAuthService(
        converterFactory: Converter.Factory,
        okHttpClient: OkHttpClient,
    ): MyAnimeListOAuthAPI {
        return Retrofit
            .Builder()
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .baseUrl(Urls.oauthBaseUrl)
            .build()
            .create(MyAnimeListOAuthAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideMALService(
        converterFactory: Converter.Factory,
        okHttpClient: OkHttpClient,
    ): MyAnimeListAPI {
        return Retrofit
            .Builder()
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .baseUrl(Urls.apiBaseUrl)
            .build()
            .create(MyAnimeListAPI::class.java)
    }
}
