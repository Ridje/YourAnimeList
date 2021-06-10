package com.kis.youranimelist.repository

import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
import com.kis.youranimelist.room.UserDatabase
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
    fun provideRepositoryNetwork(
        malService: MyAnimeListAPI,
        malOauthService: MyAnimeListOAuthAPI
    ) : RepositoryNetwork {
        return RepositoryNetworkImpl(malService, malOauthService)
    }


    @Singleton
    @Provides
    fun provideRepositoryLocal(
        roomService: UserDatabase
    ): RepositoryLocal {
        return RepositoryLocalImpl(roomService)
    }
}