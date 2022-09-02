package com.kis.youranimelist.ui.item

import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.RecommendedAnime
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import com.kis.youranimelist.ui.item.ItemScreenContract.defaultAnimeItem
import okhttp3.internal.toImmutableList

object ItemScreenContract {
    data class ScreenState(
        val item: AnimeItem,
        val listRecommendedItems: List<RecommendedAnimeItem>,
        val listRelatedItems: List<RelatedAnimeItem>,
    )

    data class RelatedAnimeItem(
        val id: Int,
        val title: String,
        val relationType: String,
        val picture: String?,
    )

    data class RecommendedAnimeItem(
        val id: Int,
        val title: String,
        val recommendedTimes: Int,
        val picture: String?,
    )

    data class AnimeItem(
        val id: Int,
        val title: String,
        val images: List<String>,
        val synopsis: String,
        val year: Int,
        val mean: Float,
        val genres: List<String>,
        val mediaType: String,
        val numEpisodes: Int,
        val airingStatus: String,
    )

    val defaultAnimeItem = AnimeItem(
        0,
        title = "Loading",
        synopsis = "Not written yet",
        year = 0,
        mean = 0.0f,
        images = listOf(),
        genres = listOf(),
        mediaType = "",
        numEpisodes = 0,
        airingStatus = "",
    )
}

fun Anime?.asAnimeItemScreen(
): ItemScreenContract.AnimeItem {
    return if (this == null) {
        defaultAnimeItem
    } else {
        ItemScreenContract.AnimeItem(
            id = this.id,
            title = this.title,
            images = mutableListOf<String>().plus(this.picture?.large)
                .plus(this.pictures.map { it.large }).filterNotNull().distinct().toImmutableList(),
            synopsis = this.synopsis ?: "Not written yet",
            mean = this.mean ?: 0.0f,
            genres = this.genres.map { it.name },
            year = this.startSeason?.year ?: 0,
            mediaType = this.mediaType?.uppercase() ?: "",
            numEpisodes = this.numEpisodes ?: 0,
            airingStatus = this.airingStatus?.replace("_", " ") ?: "",
        )
    }
}

fun RecommendedAnime.asRecommendedAnimeItemScreen(): ItemScreenContract.RecommendedAnimeItem {
    return ItemScreenContract.RecommendedAnimeItem(
        this.anime.id,
        this.anime.title,
        this.recommendedTimes,
        this.anime.picture?.large,
    )
}

fun RelatedAnime.asRelatedAnimeItemScreen(): ItemScreenContract.RelatedAnimeItem {
    return ItemScreenContract.RelatedAnimeItem(
        this.anime.id,
        this.anime.title,
        this.relatedTypeFormatted,
        this.anime.picture?.large
    )
}
