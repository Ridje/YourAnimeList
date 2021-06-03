package com.kis.youranimelist.network

import com.kis.youranimelist.utils.Urls
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideJacksonBuilder() : Converter.Factory = JacksonConverterFactory.create()

    @Singleton
    @Provides
    fun provideOkHTTPClient(authInterceptor: AuthInterceptor) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor() : AuthInterceptor {
        return AuthInterceptor()
    }


    @Singleton
    @Provides
    fun provideMALOAuthService(converterFactory: Converter.Factory, okHttpClient: OkHttpClient): MyAnimeListOAuthAPI {
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
    fun provideMALService(converterFactory: Converter.Factory, okHttpClient: OkHttpClient) : MyAnimeListAPI {
        return Retrofit
            .Builder()
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .baseUrl(Urls.apiBaseUrl)
            .build()
            .create(MyAnimeListAPI::class.java)
    }
}