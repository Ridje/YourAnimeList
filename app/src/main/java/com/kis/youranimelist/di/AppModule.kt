package com.kis.youranimelist.di

import android.content.Context
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.core.ResourceProviderImpl
import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import com.kis.youranimelist.data.network.AuthInterceptor
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.auth.AuthUseCase
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
        remoteDataSource: Lazy<RemoteDataSource>,
        appPreferences: AppPreferencesWrapper,
        authInterceptor: AuthInterceptor,
    ): AuthUseCase {
        return AuthUseCase(remoteDataSource, appPreferences, authInterceptor)
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
