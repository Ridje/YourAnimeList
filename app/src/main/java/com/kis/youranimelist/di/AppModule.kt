package com.kis.youranimelist.di

import android.content.Context
import com.kis.youranimelist.core.ResourceProviderImpl
import com.kis.youranimelist.domain.AuthUseCase
import com.kis.youranimelist.network.AuthInterceptor
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
import com.kis.youranimelist.utils.AppPreferences
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(
    includes = [UtilsModule::class, NetworkModule::class]
)
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideResourceProviderImpl(
        @ApplicationContext
        context: Context,
    ): ResourceProviderImpl {
        return ResourceProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(
        appPreferences: AppPreferences,
        authInterceptor: AuthInterceptor,
        oAuthAPI: Lazy<MyAnimeListOAuthAPI>,
    ): AuthUseCase {
        return AuthUseCase(appPreferences, authInterceptor, oAuthAPI)
    }

}
