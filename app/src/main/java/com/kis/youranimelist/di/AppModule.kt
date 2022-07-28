package com.kis.youranimelist.di

import android.content.Context
import com.kis.youranimelist.core.ResourceProviderImpl
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

}
