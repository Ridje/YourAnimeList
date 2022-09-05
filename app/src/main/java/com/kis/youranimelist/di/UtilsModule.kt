package com.kis.youranimelist.di

import android.content.Context
import com.kis.youranimelist.core.utils.AppPreferences
import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton

@Module
@DisableInstallInCheck
class UtilsModule {

    @Singleton
    @Provides
    fun provideAppPreferencesWrapper(@ApplicationContext context: Context): AppPreferencesWrapper {
        return AppPreferencesWrapper(AppPreferences(context))
    }
}
