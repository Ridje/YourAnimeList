package com.kis.youranimelist.data.repository.animelist

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.suggestingresponse.SuggestingRootResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime

class AnimeSuggestedRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val animeMapper: AnimeMapper,
) : AnimeListRepository<SuggestingRootResponse> {

    override suspend fun fetchData(
        rankingType: String,
        limit: Int,
        offset: Int,
    ): NetworkResponse<SuggestingRootResponse, ErrorResponse> {
        return remoteDataSource.getAnimeSuggestingList(
            limit,
            offset,
        ).saveToLocalDataSource(
            localDataSource::saveAnimeToCache,
        ) { mapData(it) }
    }

    override suspend fun mapData(from: SuggestingRootResponse): List<Anime> {
        return from.data.map { animeMapper.map(it) }
    }
}
