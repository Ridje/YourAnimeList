package com.kis.youranimelist.di

import com.kis.youranimelist.data.cache.AnimeRankingMemoryCache
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SideDAO
import com.kis.youranimelist.data.cache.dao.UserDAO
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.LocalDataSourceImpl
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.data.repository.RemoteDataSourceImpl
import com.kis.youranimelist.data.repository.anime.AnimeRepository
import com.kis.youranimelist.data.repository.anime.AnimeRepositoryImpl
import com.kis.youranimelist.data.repository.animeranking.AnimeRankingRepository
import com.kis.youranimelist.data.repository.animeranking.AnimeRankingRepositoryImpl
import com.kis.youranimelist.data.repository.animesearching.AnimeSearchingRepository
import com.kis.youranimelist.data.repository.animesearching.AnimeSearchingRepositoryImpl
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepositoryImpl
import com.kis.youranimelist.data.repository.user.UserRepository
import com.kis.youranimelist.data.repository.user.UserRepositoryImpl
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.user.mapper.UserMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAnimeRankingRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        animeMapper: AnimeMapper,
        cacheFactory: AnimeRankingMemoryCache.Factory,
    ): AnimeRankingRepository {
        return AnimeRankingRepositoryImpl(localDataSource,
            remoteDataSource,
            animeMapper,
            cacheFactory)
    }

    @Provides
    fun provideAnimeSearchingRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        animeMapper: AnimeMapper,
        cacheFactory: AnimeRankingMemoryCache.Factory,
    ): AnimeSearchingRepository {
        return AnimeSearchingRepositoryImpl(localDataSource,
            remoteDataSource,
            animeMapper,
            cacheFactory)
    }

    @Provides
    @Singleton
    fun provideAnimeRankingCache(): AnimeRankingMemoryCache.Factory {
        return AnimeRankingMemoryCache.Factory()
    }

    @Singleton
    @Provides
    fun provideAnimeRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        animeMapper: AnimeMapper,
        cacheFactory: AnimeRankingMemoryCache.Factory,
    ): AnimeRepository {
        return AnimeRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            animeMapper = animeMapper,
            cache = cacheFactory,
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
        dispatchers: Dispatchers,
    ): RemoteDataSource {
        return RemoteDataSourceImpl(malService, malOauthService, dispatchers)
    }


    @Singleton
    @Provides
    fun provideLocalDataSource(
        animeDAO: AnimeDAO,
        personalAnimeDAO: PersonalAnimeDAO,
        userDAO: UserDAO,
        sideDAO: SideDAO,
        dispatchers: Dispatchers,
    ): LocalDataSource {
        return LocalDataSourceImpl(personalAnimeDAO, animeDAO, userDAO, sideDAO, dispatchers)
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
