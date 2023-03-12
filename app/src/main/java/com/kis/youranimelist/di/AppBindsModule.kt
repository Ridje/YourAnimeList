package com.kis.youranimelist.di

import com.kis.youranimelist.domain.cache.RefreshCacheUseCase
import com.kis.youranimelist.domain.cache.RefreshCacheUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck

@Module
@DisableInstallInCheck
 interface AppBindsModule {

    @Binds
    fun RefreshCacheUseCaseImpl.bindRefreshCacheUseCase(): RefreshCacheUseCase
}
