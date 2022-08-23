package com.kis.youranimelist.domain.rankinglist.model

import android.os.Parcelable
import com.kis.youranimelist.data.network.model.AnimeResponse
import com.kis.youranimelist.data.network.model.ranking_response.AnimeRankingItem
import com.kis.youranimelist.data.network.model.ranking_response.RankingResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    val id: Int,
    val title: String,
    val picture: Picture? = null,
    val startSeason: Season? = null,
    val mean: Float? = null,
    val synopsis: String? = "Description",
    val genres: List<Genre> = listOf(),
    val pictures: List<Picture> = listOf(),
    val relatedAnime: List<RelatedAnime> = listOf(),
    val rank: Int? = null,
    val mediaType: String? = null,
    val numEpisodes: Int? = null,
) : Parcelable {
    constructor(animeRanked: AnimeRankingItem) : this(
        animeRanked.id,
        animeRanked.title,
        Picture(animeRanked.pictureResponse),
        Season(animeRanked.startSeasonResponse),
        null,
    )

    constructor(anime: AnimeResponse) : this(
        id = anime.id,
        title = anime.title,
        picture = Picture(anime.pictureResponse),
        startSeason = Season(anime.startSeasonResponse),
        mean = anime.mean,
        synopsis = anime.synopsis,
        genres = anime.genres?.map { Genre(it.id, it.name) } ?: listOf(),
        pictures = anime.pictures?.map { Picture(it.large, it.medium) } ?: listOf(),
        mediaType = anime.mediaType,
        numEpisodes = anime.numEpisodes,
        relatedAnime = anime.relatedAnime?.map {
            RelatedAnime(
                anime = Anime(it.node),
                relatedTypeFormatted = it.relationTypeFormatted,
                relatedType = it.relationType,
            )
        } ?: listOf(),
    )

    constructor(anime: AnimeResponse, ranking: RankingResponse) : this(
        id = anime.id,
        title = anime.title,
        picture = Picture(anime.pictureResponse),
        startSeason = Season(anime.startSeasonResponse),
        mean = anime.mean,
        synopsis = anime.synopsis,
        rank = ranking.rank,
        genres = anime.genres?.map { Genre(it.id, it.name) } ?: listOf(),
        mediaType = anime.mediaType,
        numEpisodes = anime.numEpisodes,
    )
}
