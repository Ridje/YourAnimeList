package com.kis.youranimelist.repository.personalanime

import androidx.paging.PagingSource
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.AnimeStatus

interface PersonalAnimeRepository {
    fun getDataSource(status: String): PagingSource<Int, AnimeStatus>
    suspend fun fetchData(limit: Int, offset: Int): List<AnimeStatus>
}
