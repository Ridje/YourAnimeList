package com.kis.youranimelist.di

import android.content.Context
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.core.ResourceProviderImpl
import com.kis.youranimelist.domain.AuthUseCase
import com.kis.youranimelist.domain.UserUseCase
import com.kis.youranimelist.network.AuthInterceptor
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
import com.kis.youranimelist.repository.AnimeRepository
import com.kis.youranimelist.repository.user.UserRepository
import com.kis.youranimelist.utils.AppPreferences
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.text.DateFormat
import java.util.Locale
import javax.inject.Qualifier
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
    ): ResourceProvider {
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

    @Provides
    @Singleton
    fun provideUserUseCase(
        userRepository: UserRepository,
        animeRepository: AnimeRepository,
    ): UserUseCase {
        return UserUseCase(userRepository, animeRepository)
    }

    @Provides
    @Medium
    fun provideDateFormat(
    ): DateFormat {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    }

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Medium
