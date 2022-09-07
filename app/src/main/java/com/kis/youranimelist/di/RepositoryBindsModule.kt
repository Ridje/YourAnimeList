package com.kis.youranimelist.di

import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSourceImpl
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck

@Module
@DisableInstallInCheck
interface RepositoryBindsModule {

    @Binds
    fun UserLocalDataSourceImpl.bindUserLocalDataSource(): UserLocalDataSource

    @Binds
    fun SideLocalDataSourceImpl.bindSideLocalDataSource(): SideLocalDataSource
}
