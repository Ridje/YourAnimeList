package com.kis.youranimelist.data.repository

import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.domain.user.mapper.UserMapper
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.data.repository.animeranking.AnimeRankingRepository
import com.kis.youranimelist.data.repository.animeranking.AnimeRankingRepositoryImpl
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepositoryImpl
import com.kis.youranimelist.data.repository.user.UserRepository
import com.kis.youranimelist.data.repository.user.UserRepositoryImpl
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
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
    ): UserRepository {
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
        animeDAO: AnimeDAO,
        personalAnimeDAO: PersonalAnimeDAO,
    ): LocalDataSource {
        return LocalDataSourceImpl(personalAnimeDAO, animeDAO)
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
}
