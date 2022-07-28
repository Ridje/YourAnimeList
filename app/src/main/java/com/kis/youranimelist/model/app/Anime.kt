package com.kis.youranimelist.model.app

import android.os.Parcelable
import com.kis.youranimelist.model.api.AnimeResponse
import com.kis.youranimelist.model.api.StartSeason
import com.kis.youranimelist.model.api.ranking_response.AnimeRankingItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    val id: Int,
    val title: String,
    val picture: Picture?,
    val startSeason: StartSeason?,
    val mean: Float?,
    val synopsis: String? = "Description",
    val genres: List<Genre>? = listOf(),
    val pictures: List<Picture> = listOf(),
    val relatedAnime: List<RelatedAnime> = listOf(),
) : Parcelable {
    constructor(animeRanked: AnimeRankingItem) : this(
        animeRanked.id,
        animeRanked.title,
        Picture(animeRanked.pictureResponse),
        animeRanked.startSeason,
        null,
        null,
    )

    constructor(anime: AnimeResponse) : this(
        anime.id,
        anime.title,
        Picture(anime.pictureResponse),
        anime.startSeason,
        anime.mean,
        anime.synopsis,
        anime.genres?.map { Genre(it.id, it.name) } ?: listOf(),
        anime.pictures?.map { Picture(it.large, it.medium) } ?: listOf(),
        anime.relatedAnime?.map {
            RelatedAnime(
                anime = Anime(it.node),
                relatedTypeFormatted = it.relationTypeFormatted,
                relatedType = it.relationType,
            )
        } ?: listOf(),
    )
}
