package com.kis.youranimelist.di

import android.content.Context
import androidx.work.WorkManager
import com.kis.youranimelist.data.cache.AnimeRankingMemoryCache
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SyncJobDao
import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.data.network.model.rankingresponse.RankingRootResponse
import com.kis.youranimelist.data.network.model.searchresponse.SearchingRootResponse
import com.kis.youranimelist.data.network.model.suggestingresponse.SuggestingRootResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.LocalDataSourceImpl
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.data.repository.RemoteDataSourceImpl
import com.kis.youranimelist.data.repository.anime.AnimeRepository
import com.kis.youranimelist.data.repository.anime.AnimeRepositoryImpl
import com.kis.youranimelist.data.repository.animelist.AnimeListRepository
import com.kis.youranimelist.data.repository.animelist.AnimeRankingRepositoryImpl
import com.kis.youranimelist.data.repository.animelist.AnimeSearchingRepositoryImpl
import com.kis.youranimelist.data.repository.animelist.AnimeSuggestedRepositoryImpl
import com.kis.youranimelist.data.repository.pagingsource.AnimeListPagingRepository
import com.kis.youranimelist.data.repository.pagingsource.AnimeListPagingRepositoryImpl
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepositoryImpl
import com.kis.youranimelist.data.repository.user.UserRepository
import com.kis.youranimelist.data.repository.user.UserRepositoryImpl
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.user.mapper.UserMapper
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(includes = [RepositoryBindsModule::class])
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Search
    fun provideAnimeSearchPagingRepository(
        cacheFactory: AnimeRankingMemoryCache.Factory,
        animeListRepository: AnimeListRepository<SearchingRootResponse>,
    ): AnimeListPagingRepository {
        return AnimeListPagingRepositoryImpl(cacheFactory,
            animeListRepository)
    }

    @Provides
    @Ranking
    fun provideAnimeRankingPagingRepository(
        cacheFactory: AnimeRankingMemoryCache.Factory,
        animeListRepository: AnimeListRepository<RankingRootResponse>,
    ): AnimeListPagingRepository {
        return AnimeListPagingRepositoryImpl(cacheFactory,
            animeListRepository)
    }

    @Provides
    @Suggestions
    fun provideAnimeSuggestionsPagingRepository(
        cacheFactory: AnimeRankingMemoryCache.Factory,
        animeListRepository: AnimeListRepository<SuggestingRootResponse>,
    ): AnimeListPagingRepository {
        return AnimeListPagingRepositoryImpl(cacheFactory, animeListRepository)
    }

    @Provides
    fun provideAnimeRankingListRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        animeMapper: AnimeMapper,
    ): AnimeListRepository<RankingRootResponse> {
        return AnimeRankingRepositoryImpl(remoteDataSource, localDataSource, animeMapper)
    }

    @Provides
    fun provideAnimeSearchingListRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        animeMapper: AnimeMapper,
    ): AnimeListRepository<SearchingRootResponse> {
        return AnimeSearchingRepositoryImpl(remoteDataSource, localDataSource, animeMapper)
    }

    @Provides
    fun provideAnimeSuggestionsListRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        animeMapper: AnimeMapper,
    ): AnimeListRepository<SuggestingRootResponse> {
        return AnimeSuggestedRepositoryImpl(remoteDataSource, localDataSource, animeMapper)
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
        @Dispatcher(YALDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): RemoteDataSource {
        return RemoteDataSourceImpl(malService, malOauthService, ioDispatcher)
    }


    @Singleton
    @Provides
    fun provideLocalDataSource(
        database: UserDatabase,
        animeDAO: AnimeDAO,
        personalAnimeDAO: PersonalAnimeDAO,
        userLocalDataSource: UserLocalDataSource,
        sideLocalDataSource: SideLocalDataSource,
        syncJobDao: SyncJobDao,
        @Dispatcher(YALDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): LocalDataSource {
        return LocalDataSourceImpl(database,
            personalAnimeDAO,
            animeDAO,
            userLocalDataSource,
            sideLocalDataSource,
            syncJobDao,
            ioDispatcher)
    }

    @Singleton
    @Provides
    fun providePersonalAnimeRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        animeStatusMapper: AnimeStatusMapper,
        workManager: Lazy<WorkManager>,
    ): PersonalAnimeRepository {
        return PersonalAnimeRepositoryImpl(
            remoteDataSource,
            localDataSource,
            animeStatusMapper,
            workManager
        )
    }

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Search

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Ranking

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Suggestions
