package com.kis.youranimelist.repository

import com.kis.youranimelist.model.mapper.UserMapper
import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
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
        userMapper: UserMapper,
    ): RemoteDataSource {
        return RemoteDataSourceImpl(malService, malOauthService, userMapper)
    }


    @Singleton
    @Provides
    fun provideLocalDataSource(
        roomService: UserDatabase,
    ): LocalDataSource {
        return LocalDataSourceImpl(roomService)
    }
}
