package com.kis.youranimelist.di

import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.UserDAO
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.data.repository.AnimeRepository
import com.kis.youranimelist.data.repository.AnimeRepositoryImpl
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.LocalDataSourceImpl
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.data.repository.RemoteDataSourceImpl
import com.kis.youranimelist.data.repository.animeranking.AnimeRankingRepository
import com.kis.youranimelist.data.repository.animeranking.AnimeRankingRepositoryImpl
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepositoryImpl
import com.kis.youranimelist.data.repository.user.UserRepository
import com.kis.youranimelist.data.repository.user.UserRepositoryImpl
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.user.mapper.UserMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
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
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        userMapper: UserMapper,
    ): UserRepository {
        return UserRepositoryImpl(localDataSource, remoteDataSource, userMapper)
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
        animeDAO: AnimeDAO,
        personalAnimeDAO: PersonalAnimeDAO,
        userDAO: UserDAO,
        dispatchers: Dispatchers,
    ): LocalDataSource {
        return LocalDataSourceImpl(personalAnimeDAO, animeDAO, userDAO, dispatchers)
    }

    @Singleton
    @Provides
    fun providePersonalAnimeRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        animeStatusMapper: AnimeStatusMapper,
    ): PersonalAnimeRepository {
        return PersonalAnimeRepositoryImpl(remoteDataSource, localDataSource, animeStatusMapper)
    }

    @Singleton
    @Provides
    fun provideCoroutineDispatchers(): Dispatchers {
        return Dispatchers
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Cache

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Network
