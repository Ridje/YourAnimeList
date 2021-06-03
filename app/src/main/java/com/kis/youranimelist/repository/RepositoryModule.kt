package com.kis.youranimelist.repository

import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        malService : MyAnimeListAPI,
        malOauthService : MyAnimeListOAuthAPI
    ) : RepositoryNetwork {
        return RepositoryNetwork(malService, malOauthService)
    }

}