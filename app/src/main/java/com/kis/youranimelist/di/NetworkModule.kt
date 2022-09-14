package com.kis.youranimelist.di

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import com.kis.youranimelist.core.utils.Setting
import com.kis.youranimelist.core.utils.Urls
import com.kis.youranimelist.data.network.AuthInterceptor
import com.kis.youranimelist.data.network.AuthMode
import com.kis.youranimelist.data.network.MALTokenAuthenticator
import com.kis.youranimelist.data.network.NSFWInterceptor
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.domain.auth.AuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
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
        nsfwInterceptor: NSFWInterceptor,
        malTokenAuthenticator: MALTokenAuthenticator,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(malTokenAuthenticator)
            .addInterceptor(authInterceptor)
            .addInterceptor(nsfwInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(appPreferences: AppPreferencesWrapper): AuthInterceptor {
        return AuthInterceptor().apply {
            setAuthorization(
                if (appPreferences.readValue(Setting.UseAppAuth)) {
                    AuthMode.AppToken(BuildConfig.CLIENT_ID)
                } else {
                    AuthMode.UserToken(
                        appPreferences.readValue(Setting.TypeToken),
                        appPreferences.readValue(Setting.AccessToken),
                        appPreferences.readValue(Setting.RefreshToken)
                    )
                }
            )
        }
    }

    @Singleton
    @Provides
    fun provideNSFWInterceptor(appPreferencesWrapper: AppPreferencesWrapper): NSFWInterceptor {
        return NSFWInterceptor(appPreferencesWrapper).apply {
            this.nsfw.set(appPreferencesWrapper.readValue(Setting.NSFW))
        }
    }

    @Singleton
    @Provides
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return NetworkResponseAdapterFactory()
    }

    @Singleton
    @Provides
    fun provideMALOAuthService(
        converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory,
        okHttpClient: OkHttpClient,
    ): MyAnimeListOAuthAPI {
        return Retrofit
            .Builder()
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .client(okHttpClient)
            .baseUrl(Urls.oauthBaseUrl)
            .build()
            .create(MyAnimeListOAuthAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideMALService(
        converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory,
        okHttpClient: OkHttpClient,
    ): MyAnimeListAPI {
        return Retrofit
            .Builder()
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .client(okHttpClient)
            .baseUrl(Urls.apiBaseUrl)
            .build()
            .create(MyAnimeListAPI::class.java)
    }
}
