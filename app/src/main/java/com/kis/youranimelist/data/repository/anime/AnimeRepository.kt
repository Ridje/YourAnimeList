package com.kis.youranimelist.data.repository.anime

import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.model.AnimeRankType
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {

    suspend fun refreshAnimeDetailedData(animeID: Int): Boolean

    suspend fun getRankingAnimeList(
        rankingType: AnimeRankType,
        limit: Int?,
        offset: Int?,
    ): ResultWrapper<List<Anime>>

    fun getAnimeDetailedDataSource(animeID: Int): Flow<Anime>
}
