package com.kis.youranimelist.ui.item

import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import okhttp3.internal.toImmutableList

object ItemScreenContract {
    data class ScreenState(
        val item: AnimeItem,
    )
}

data class AnimeItem(
    val id: Int,
    val title: String,
    val images: List<String>,
    val synopsis: String,
    val year: Int,
    val mean: Float,
    val genres: String,
    val relatedAnime: List<RelatedAnimeItem> = listOf(),
)

val defaultAnimeItem = AnimeItem(
    0,
    title = "Loading",
    synopsis = "Not written yet",
    year = 0,
    mean = 0.0f,
    images = listOf(),
    genres = "",
)

data class RelatedAnimeItem(
    val id: Int,
    val title: String,
    val relationType: String,
    val picture: String?,
)


fun Anime?.asAnimeItemScreen(
    relatedAnimeMapper: (RelatedAnime) -> RelatedAnimeItem = RelatedAnime::asRelatedAnimeItemScreen,
): AnimeItem {
    if (this == null) {
        return defaultAnimeItem
    } else {
        return AnimeItem(
            id = this.id,
            title = this.title,
            images = mutableListOf<String>().plus(this.picture?.large)
                .plus(this.pictures.map { it.large }).filterNotNull().distinct().toImmutableList(),
            synopsis = this.synopsis ?: "Not written yet",
            mean = this.mean ?: 0.0f,
            genres = this.genres.map { it.name }.take(3).joinToString(separator = ", "),
            year = this.startSeason?.year ?: 0,
            relatedAnime = this.relatedAnime.map {
                relatedAnimeMapper(it)
            }
        )
    }
}

fun RelatedAnime.asRelatedAnimeItemScreen(): RelatedAnimeItem {
    return RelatedAnimeItem(this.anime.id,
        this.anime.title,
        this.relatedTypeFormatted,
        this.anime.picture?.large
    )
}
