package com.kis.youranimelist.data.repository.personalanime

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.core.utils.returnCatchingWithCancellation
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.personallist.PersonalAnimeItemResponse
import com.kis.youranimelist.data.network.model.personallist.PersonalAnimeListResponse
import com.kis.youranimelist.data.network.model.personallist.asAnimePersonalStatusPersistence
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.data.repository.synchronization.SynchronizationManager
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val MAX_ITEMS_PER_REQUEST = 1000

class PersonalAnimeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val animeStatusMapper: AnimeStatusMapper,
    private val synchronizationManager: SynchronizationManager,
) : PersonalAnimeRepository {

    override fun getPersonalAnimeStatusesProducer(): Flow<List<AnimeStatus>> {
        return localDataSource.getAnimeWithStatusProducer()
            .map { entities -> entities.map { entity -> animeStatusMapper.map(entity) } }
    }

    override suspend fun deleteSyncPersonalData(): Boolean {
        synchronizationManager.cancelPlannedSynchronizations()
        return localDataSource.deleteSyncData()
    }

    override suspend fun deletePersonalAnimeStatus(animeId: Int): Boolean {
        val localResult = localDataSource.deleteAnimePersonalStatus(animeId)
        val remoteResult = remoteDataSource.deletePersonalAnimeStatus(animeId)

        if (remoteResult is NetworkResponse.Error) {
            synchronizationManager.planSynchronization()
        } else {
            localDataSource.removePersonalAnimeListSyncJob(
                animeId,
            )
        }

        return localResult
    }

    override suspend fun refreshPersonalAnimeStatus(animeId: Int) {
        val remoteResult = remoteDataSource.getPersonalAnimeStatus(animeId)
        if (remoteResult is NetworkResponse.Success) {
            localDataSource.mergePersonalAnimeStatus(
                remoteResult.body.asAnimePersonalStatusPersistence(animeId) { updatedAt ->
                    AnimeStatusMapper.formatter.parse(updatedAt)?.time ?: Long.MIN_VALUE
                }
            )
        }
    }

    override fun getPersonalAnimeStatusProducer(id: Int): Flow<AnimeStatus?> {
        return localDataSource
            .getAnimeWithStatusProducer(id)
            .map { entity -> entity?.let { animeStatusMapper.map(entity) } }
    }

    override suspend fun saveAnimeStatus(animeStatus: AnimeStatus): Boolean {
        val localResult = localDataSource.savePersonalAnimeStatus(
            AnimePersonalStatusPersistence(
                score = animeStatus.score,
                episodesWatched = animeStatus.numWatchedEpisodes,
                statusId = animeStatus.status.presentIndex,
                animeId = animeStatus.anime.id,
                updatedAt = animeStatus.updatedAt,
                comments = animeStatus.comments,
            )
        )
        val remoteResult = remoteDataSource.savePersonalAnimeStatus(
            animeStatus.anime.id,
            animeStatus.status.presentIndex,
            animeStatus.score,
            animeStatus.numWatchedEpisodes,
        )

        if (remoteResult is NetworkResponse.Error) {
            synchronizationManager.planSynchronization()
        } else {
            localDataSource.removePersonalAnimeListSyncJob(
                animeStatus.anime.id,
            )
        }

        return localResult
    }

    override suspend fun synchronize(): Boolean {

        val syncJobs =
            localDataSource.getPersonalAnimeListSyncJobs().associateWith { false }.toMutableMap()

        val remoteList = fetchAllData().map { animeStatusMapper.map(it) }

        for (job in syncJobs.filter { it.key.deleted }) {
            val foundRemoteStatus = remoteList.find { it.anime.id == job.key.animeId }
            if (foundRemoteStatus != null) {
                if (job.key.changeTimestamp > foundRemoteStatus.updatedAt) {
                    val result = remoteDataSource.deletePersonalAnimeStatus(job.key.animeId)
                    if (result is NetworkResponse.Success) {
                        syncJobs[job.key] = true
                    }
                } else {
                    localDataSource.savePersonalAnimeStatus(
                        AnimePersonalStatusPersistence(
                            score = foundRemoteStatus.score,
                            episodesWatched = foundRemoteStatus.numWatchedEpisodes,
                            statusId = foundRemoteStatus.status.presentIndex,
                            animeId = foundRemoteStatus.anime.id,
                            updatedAt = foundRemoteStatus.updatedAt,
                            comments = foundRemoteStatus.comments,
                        )
                    )
                    syncJobs[job.key] = true
                }
            } else {
                syncJobs[job.key] = true
            }
        }

        for (job in syncJobs.filter { !it.key.deleted }) {
            val foundRemoteStatus = remoteList.find { it.anime.id == job.key.animeId }
            if (foundRemoteStatus == null || job.key.changeTimestamp > foundRemoteStatus.updatedAt) {
                val localValue = localDataSource.getAnimePersonalStatus(job.key.animeId)
                val result = remoteDataSource.savePersonalAnimeStatus(job.key.animeId,
                    localValue?.statusId,
                    localValue?.score,
                    localValue?.episodesWatched
                )
                if (result is NetworkResponse.Success) {
                    syncJobs[job.key] = true
                }
            } else if (job.key.changeTimestamp < foundRemoteStatus.updatedAt) {
                localDataSource.saveAnimeWithPersonalStatus(foundRemoteStatus)
                syncJobs[job.key] = true
            }
        }

        returnCatchingWithCancellation {
            localDataSource.saveAnimeWithPersonalStatus(remoteList.filter { remoteAnime ->
                syncJobs.keys.find { job -> job.animeId == remoteAnime.anime.id } == null
            })
        }

        val finishedJobs = syncJobs.filter { it.value }
        if (finishedJobs.isNotEmpty()) {
            localDataSource.removePersonalAnimeListSyncJob(finishedJobs.keys.toList())
        }
        val notFinishedJobs = syncJobs.filter { it.value == false }
        return notFinishedJobs.isEmpty()
    }

    override suspend fun fetchData(
        limit: Int,
        offset: Int,
    ): NetworkResponse<PersonalAnimeListResponse, ErrorResponse> {
        return remoteDataSource.getPersonalAnimeList(null, null, limit, offset)
    }

    override suspend fun fetchAllData(): List<PersonalAnimeItemResponse> {
        val fetchedData = mutableListOf<PersonalAnimeItemResponse>()
        var hasNext = true
        while (hasNext) {
            when (val fetchedValue = fetchData(MAX_ITEMS_PER_REQUEST, 0)) {
                is NetworkResponse.Error -> hasNext = false
                is NetworkResponse.Success -> {
                    if (fetchedValue.body.paging.next.isNullOrBlank()) {
                        hasNext = false
                    }
                    fetchedData.addAll(fetchedValue.body.data)
                }
            }
        }
        return fetchedData
    }
}

