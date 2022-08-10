package com.kis.youranimelist.data.repository.animeranking

import androidx.paging.PagingSource
import com.kis.youranimelist.domain.rankinglist.model.Anime

interface AnimeRankingRepository{
    suspend fun fetchData(rankingType: String, limit: Int, offset: Int): List<Anime>
    fun getDataSource(rankingType: String): PagingSource<Int, Anime>
}
