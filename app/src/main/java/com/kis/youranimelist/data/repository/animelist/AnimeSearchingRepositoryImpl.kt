package com.kis.youranimelist.data.repository.animelist

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.searchresponse.SearchingRootResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import javax.inject.Inject

class AnimeSearchingRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val animeMapper: AnimeMapper,
) : AnimeListRepository<SearchingRootResponse> {

    override suspend fun fetchData(
        rankingType: String,
        limit: Int,
        offset: Int,
    ): NetworkResponse<SearchingRootResponse, ErrorResponse> {
        return remoteDataSource.getAnimeSearchList(rankingType, limit, offset)
            .saveToLocalDataSource(
                localDataSource::saveAnimeToCache,
            ) { mapData(it) }
    }

    override suspend fun mapData(from: SearchingRootResponse): List<Anime> {
        return from.data.map { animeMapper.map(it) }
    }
}

