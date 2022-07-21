package com.kis.youranimelist.di

import com.kis.youranimelist.network.AuthInterceptor
import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
import com.kis.youranimelist.utils.AppPreferences
import com.kis.youranimelist.utils.Urls
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@DisableInstallInCheck
object NetworkModule {

    @Singleton
    @Provides
    fun provideJacksonBuilder(): Converter.Factory = JacksonConverterFactory.create()

    @Singleton
    @Provides
    fun provideOkHTTPClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
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
                appPreferences.readString(AppPreferences.ACCESS_TOKEN_SETTING_KEY)
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
