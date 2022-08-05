package com.kis.youranimelist.repository.animeranking

import androidx.paging.PagingSource
import com.kis.youranimelist.model.app.Anime

interface AnimeRankingRepository{
    suspend fun fetchData(rankingType: String, limit: Int, offset: Int): List<Anime>
    fun getDataSource(rankingType: String): PagingSource<Int, Anime>
}
