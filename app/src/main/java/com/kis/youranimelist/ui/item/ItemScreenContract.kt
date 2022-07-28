package com.kis.youranimelist.ui.item

import com.kis.youranimelist.model.app.Anime
import okhttp3.internal.toImmutableList

object ItemScreenContract {
    data class ScreenState(
        val item: AnimeItem,
    )
}

data class AnimeItem(
    val title: String,
    val images: List<String>,
    val synopsis: String,
    val year: Int,
    val mean: Float,
    val genres: String,
    val relatedAnime: List<RelatedAnimeItem> = listOf(),
)

object ItemScreenMapper {
    fun map(anime: Anime?): AnimeItem {

        if (anime == null) {
            return AnimeItem(
                title = "Loading",
                synopsis = "Not written yet",
                year = 0,
                mean = 0.0f,
                images = listOf(),
                genres = "",
            )
        } else {
            return AnimeItem(
                title = anime.title,
                images = mutableListOf<String>().plus(anime.picture?.large)
                    .plus(anime.pictures.map { it.large }).filterNotNull().distinct().toImmutableList(),
                synopsis = anime.synopsis ?: "Not written yet",
                mean = anime.mean ?: 0.0f,
                genres = anime.genres?.map { it.name }?.take(3)?.joinToString(separator = ", " ) ?: "",
                year = anime.startSeason?.year ?: 0,
                relatedAnime = anime.relatedAnime.map { RelatedAnimeItem(it.anime.id, it.anime.title, it.relatedTypeFormatted, it.anime.picture?.large) }
            )
        }
    }
}

data class RelatedAnimeItem(
    val id: Int,
    val title: String,
    val relationType: String,
    val picture: String?,
)
