package com.kis.youranimelist.domain.rankinglist.mapper

import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.network.model.rankingresponse.AnimeRankedResponse
import com.kis.youranimelist.data.network.model.searchresponse.AnimeSearchResponse
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.Genre
import com.kis.youranimelist.domain.rankinglist.model.Picture
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import com.kis.youranimelist.domain.rankinglist.model.Season
import javax.inject.Inject

class AnimeMapper @Inject constructor(

) {
    fun map(from: AnimeRankedResponse): Anime {
        return Anime(from.anime, from.ranking)
    }

    fun map(from: AnimeSearchResponse): Anime {
        return Anime(from.anime)
    }

    fun map(from: AnimePersistence): Anime {
        return Anime(
            id = from.id,
            title = from.title,
            numEpisodes = from.numEpisodes,
            picture = null,
            mean = from.mean,
            mediaType = from.mediaType,
        )
    }

    fun map(from: AnimePersistence, mainPicture: PicturePersistence): Anime {
        return Anime(
            id = from.id,
            title = from.title,
            numEpisodes = from.numEpisodes,
            picture = Picture(
                mainPicture.large,
                mainPicture.medium,
            ),
            mean = from.mean,
            mediaType = from.mediaType,
        )
    }

    fun map(
        from: AnimeDetailedDataPersistence,
        listRelatedAnimePictures: List<PicturePersistence?>,
    ): Anime {
        return Anime(
            id = from.anime.id,
            title = from.anime.title,
            picture = Picture(from.mainPicture.large, from.mainPicture.medium),
            startSeason = Season(from.startSeason.year, from.startSeason.season),
            mean = from.anime.mean,
            synopsis = from.anime.synopsis,
            genres = from.genres.map { Genre(it.id, it.name) },
            pictures = from.pictures.map { Picture(it.large, it.medium) },
            relatedAnime = from.relatedAnime
                .mapIndexed { index, anime ->
                    RelatedAnime(
                        anime = listRelatedAnimePictures[index]?.let { mainPicture ->
                            this.map(anime,
                                mainPicture)
                        } ?: this.map(anime),
                        relatedTypeFormatted = from.relatedAnimeAdditionValues[index].relatedTypeFormatted,
                        relatedType = from.relatedAnimeAdditionValues[index].relatedType)
                },
            mediaType = from.anime.mediaType,
            numEpisodes = from.anime.numEpisodes,
        )
    }

}
