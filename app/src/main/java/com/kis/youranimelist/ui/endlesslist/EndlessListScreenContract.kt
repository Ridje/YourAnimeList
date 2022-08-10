package com.kis.youranimelist.ui.endlesslist

import androidx.paging.PagingData
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow

object EndlessListScreenContract {
    data class ScreenState(
        val items: Flow<PagingData<Item>>,
    )
}

data class Item(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val name: String?,
    val description: String?,
    val mean: Float?,
    val rank: Int?,
    val genres: String?,
    val year: Int?,
    val numEpisodes: Int?,
    val mediaType: String?,
)

object EndlessListScreenMapper {
    fun map(anime: Anime?): Item {
        return Item(
            id = anime?.id ?: -1,
            title = anime?.title ?: "",
            imageUrl = anime?.picture?.large,
            name = anime?.title,
            description = anime?.synopsis,
            mean = anime?.mean,
            rank = anime?.rank,
            genres = anime?.genres?.map { it.name }?.take(3)?.joinToString(separator = ", ") ?: "",
            year = anime?.startSeason?.year,
            mediaType = anime?.mediaType,
            numEpisodes = anime?.numEpisodes,
        )
    }
}
