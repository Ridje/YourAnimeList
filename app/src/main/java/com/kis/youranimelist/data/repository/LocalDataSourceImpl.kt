package com.kis.youranimelist.data.repository

import androidx.room.withTransaction
import com.kis.youranimelist.core.utils.returnFinishedCatchingWithCancellation
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.localdatasource.AnimeLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.PersonalAnimeLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SyncJobLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalDataSourceImpl(
    private val database: UserDatabase,
    private val personalAnimeLocalDataSource: PersonalAnimeLocalDataSource,
    private val animeLocalDataSource: AnimeLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val sideLocalDataSource: SideLocalDataSource,
    private val syncJobLocalDataSource: SyncJobLocalDataSource,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LocalDataSource,
    UserLocalDataSource by userLocalDataSource,
    SideLocalDataSource by sideLocalDataSource,
    SyncJobLocalDataSource by syncJobLocalDataSource,
    AnimeLocalDataSource by animeLocalDataSource,
    PersonalAnimeLocalDataSource by personalAnimeLocalDataSource {

    override suspend fun deleteSyncData(): Boolean {
        return withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                database.withTransaction {
                    syncJobLocalDataSource.deleteAllSyncJobs()
                    personalAnimeLocalDataSource.deleteAllPersonalStatuses()
                }
            }
        }
    }
}
