package com.kis.youranimelist.data.cache.localdatasource

import com.kis.youranimelist.core.utils.returnCatchingWithCancellation
import com.kis.youranimelist.data.cache.dao.SyncJobDao
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SyncJobLocalDataSource {
    suspend fun deleteAllSyncJobs()
    suspend fun addPersonalAnimeListSyncJob(deferredPersonalAnimeListChangeJob: DeferredPersonalAnimeListChange)
    suspend fun getPersonalAnimeListSyncJobs(): List<DeferredPersonalAnimeListChange>
    suspend fun removePersonalAnimeListSyncJob(animeId: Int)
    suspend fun removePersonalAnimeListSyncJob(deferredPersonalAnimeListChangeJobs: List<DeferredPersonalAnimeListChange>)
}

class SyncJobLocalDataSourceImpl @Inject constructor(
    private val syncJobDao: SyncJobDao,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SyncJobLocalDataSource {
    override suspend fun deleteAllSyncJobs() {
        withContext(ioDispatcher) {
            returnCatchingWithCancellation { syncJobDao.deleteAllSyncJobs() }
        }
    }

    override suspend fun addPersonalAnimeListSyncJob(deferredPersonalAnimeListChangeJob: DeferredPersonalAnimeListChange) {
        withContext(ioDispatcher) {
            returnCatchingWithCancellation {
                syncJobDao.addPersonalAnimeListSyncJob(deferredPersonalAnimeListChangeJob)
            }
        }
    }

    override suspend fun getPersonalAnimeListSyncJobs(): List<DeferredPersonalAnimeListChange> =
        withContext(ioDispatcher) {
            returnCatchingWithCancellation { syncJobDao.getPersonalAnimeListSyncJobs() } ?: listOf()
        }

    override suspend fun removePersonalAnimeListSyncJob(animeId: Int) {
        withContext(ioDispatcher) {
            returnCatchingWithCancellation { syncJobDao.deletePersonalAnimeListSyncJob(animeId) }
        }
    }

    override suspend fun removePersonalAnimeListSyncJob(deferredPersonalAnimeListChangeJobs: List<DeferredPersonalAnimeListChange>) {
        withContext(ioDispatcher) {
            returnCatchingWithCancellation {
                syncJobDao.deletePersonalAnimeListSyncJob(deferredPersonalAnimeListChangeJobs)
            }
        }
    }
}
