package com.kis.youranimelist.di

import com.kis.youranimelist.domain.cache.ClearCacheUseCase
import com.kis.youranimelist.domain.cache.ClearCacheUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface DomainBindsModule {

    @Binds
    fun ClearCacheUseCaseImpl.bindClearCacheUseCase(): ClearCacheUseCase
}
