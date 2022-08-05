package com.kis.youranimelist.repository

import com.kis.youranimelist.model.mapper.AnimeMapper
import com.kis.youranimelist.model.mapper.UserMapper
import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
import com.kis.youranimelist.repository.animeranking.AnimeRankingRepository
import com.kis.youranimelist.repository.animeranking.AnimeRankingRepositoryImpl
import com.kis.youranimelist.repository.user.UserRepository
import com.kis.youranimelist.repository.user.UserRepositoryImpl
import com.kis.youranimelist.room.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAnimeRankingRepository(
        remoteDataSource: RemoteDataSource,
        animeMapper: AnimeMapper,
    ): AnimeRankingRepository {
        return AnimeRankingRepositoryImpl(remoteDataSource, animeMapper)
    }

    @Singleton
    @Provides
    fun provideAnimeRepository(
        remoteDataSource: RemoteDataSource,
    ): AnimeRepository {
        return AnimeRepositoryImpl(
            remoteDataSource,
        )
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        remoteDataSource: RemoteDataSource,
        userMapper: UserMapper,
    ) : UserRepository {
        return UserRepositoryImpl(remoteDataSource, userMapper)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(
        malService: MyAnimeListAPI,
        malOauthService: MyAnimeListOAuthAPI,
    ): RemoteDataSource {
        return RemoteDataSourceImpl(malService, malOauthService)
    }


    @Singleton
    @Provides
    fun provideLocalDataSource(
        roomService: UserDatabase,
    ): LocalDataSource {
        return LocalDataSourceImpl(roomService)
    }
}
