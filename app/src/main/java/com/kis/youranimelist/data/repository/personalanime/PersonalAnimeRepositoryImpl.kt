package com.kis.youranimelist.data.repository.personalanime

import android.util.Log
import com.kis.youranimelist.data.cache.model.AnimePersonalStatusEntity
import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "PersonalAnimeRepositoryImpl"

class PersonalAnimeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val animeStatusMapper: AnimeStatusMapper,
) : PersonalAnimeRepository {

    override fun getPersonalAnimeStatusesProducer(): Flow<List<AnimeStatus>> {
        return localDataSource.getAnimeWithStatusProducerFromCache()
            .map { entities -> entities.map { entity -> animeStatusMapper.map(entity) } }
    }

    override suspend fun refreshPersonalAnimeStatuses() {
        var hasNext = true;
        val dataList = mutableListOf<AnimeStatus>()
        while (hasNext) {
            val fetchedValue = fetchData(1000, 0)
            if (fetchedValue.paging.next.isNullOrBlank()) {
                hasNext = false
            }
            dataList.addAll(fetchedValue.data.map { animeStatusMapper.map(it) })
        }
        try {
            localDataSource.saveAnimeWithPersonalStatusToCache(dataList)
        } catch (e: Exception) {
            Log.d(TAG, "Error during cache invalidation, error message: ${e.message}")
        }
    }

    override suspend fun deletePersonalAnimeStatus(animeId: Int): Boolean {
        val localResult = localDataSource.deleteAnimePersonalStatusFromCache(animeId)
        val remoteResult = remoteDataSource.deletePersonalAnimeStatus(animeId)

        return localResult
    }

    override suspend fun refreshPersonalAnimeStatus(animeId: Int) {
        remoteDataSource.getPersonalAnimeStatus(animeId)?.let {
            localDataSource.savePersonalAnimeStatusToCache(
                AnimePersonalStatusEntity(
                    score = it.score,
                    episodesWatched = it.numEpisodesWatched,
                    statusId = it.status,
                    animeId = animeId,
                )
            )
        }
    }

    override fun getPersonalAnimeStatusProducer(id: Int): Flow<AnimeStatus?> {
        return localDataSource
            .getAnimeWithStatusProducerFromCache(id)
            .map { entity -> entity?.let { animeStatusMapper.map(entity) } }
    }

    override suspend fun saveAnimeStatus(animeStatus: AnimeStatus): Boolean {
        val localResult = localDataSource.savePersonalAnimeStatusToCache(
            AnimePersonalStatusEntity(
                score = animeStatus.score,
                episodesWatched = animeStatus.numWatchedEpisodes,
                statusId = animeStatus.status.presentIndex,
                animeId = animeStatus.anime.id,
            )
        )
        val remoteResult = remoteDataSource.savePersonalAnimeStatus(
            animeStatus.anime.id,
            animeStatus.status.presentIndex,
            animeStatus.score,
            animeStatus.numWatchedEpisodes,
        )
        return localResult
    }


    override suspend fun fetchData(limit: Int, offset: Int): PersonalAnimeListResponse {
        return remoteDataSource.getPersonalAnimeList(null, null, limit, offset)
    }
}
