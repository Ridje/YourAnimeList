package com.kis.youranimelist.ui.endlesslist

import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow

object EndlessListScreenContract {
    data class ScreenState(
        val items: Flow<PagingData<EndlessListItem>>,
        val title: String,
    )

    interface ScreenEventsListener {
        fun onReloadClicked(items: LazyPagingItems<EndlessListItem>)
    }
}

data class EndlessListItem(
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

fun Anime?.asEndlessListItem(): EndlessListItem {
    return EndlessListItem(
        id = this?.id ?: -1,
        title = this?.title ?: "",
        imageUrl = this?.picture?.large,
        name = this?.title,
        description = this?.synopsis,
        mean = this?.mean,
        rank = this?.rank,
        genres = this?.genres?.map { it.name }?.take(3)?.joinToString(separator = ", "),
        year = this?.startSeason?.year,
        mediaType = this?.mediaType,
        numEpisodes = this?.numEpisodes,
    )
}
