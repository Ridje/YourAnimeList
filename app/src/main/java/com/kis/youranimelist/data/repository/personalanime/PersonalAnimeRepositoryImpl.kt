package com.kis.youranimelist.data.repository.personalanime

import android.util.Log
import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "PersonalAnimeRepositoryImpl"

class PersonalAnimeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val animeStatusMapper: AnimeStatusMapper,
) : PersonalAnimeRepository {

    override fun getAllDataProducer() = flow {

        val personalAnimeLocalCache = localDataSource.getPersonalAnimeStatusFromCache()
        if (personalAnimeLocalCache.isNotEmpty()) {
            emit(personalAnimeLocalCache.map { animeStatusMapper.map(it) })
        }

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
        emit(dataList)
    }

    override fun getPersonalAnimeStatusProducer(id: Int) = flow {
        localDataSource.getPersonalAnimeStatusFromCache(id)?.let { cachedStatus ->
            emit(animeStatusMapper.map(cachedStatus))
        }
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
        dataList.firstOrNull { it.anime.id == id }?.let { updatedStatus ->
            emit(updatedStatus)
        }
    }

    override suspend fun saveAnimeStatus(animeStatus: AnimeStatus): Boolean {
        val localResult = localDataSource.savePersonalAnimeStatusToCache(animeStatus)
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
