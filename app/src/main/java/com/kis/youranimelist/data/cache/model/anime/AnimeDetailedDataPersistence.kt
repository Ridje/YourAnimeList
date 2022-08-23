package com.kis.youranimelist.data.cache.model.anime

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence

data class AnimeDetailedDataPersistence(
    @Embedded
    val anime: AnimePersistence,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RelatedAnimePersistence::class,
            parentColumn = "anime_id",
            entityColumn = "related_anime_id"
        )
    )
    val relatedAnime: List<AnimePersistence>,

    @Relation(
        parentColumn = "id",
        entityColumn = "anime_id",
    )
    val relatedAnimeAdditionValues: List<RelatedAnimePersistence>,

    @Relation(
        parentColumn = "picture_id",
        entityColumn = "id"
    )
    val mainPicture: PicturePersistence,

    @Relation(
        parentColumn = "start_season_id",
        entityColumn = "id"
    )
    val startSeason: SeasonPersistence,

    @Relation(
        parentColumn = "id",
        entityColumn = "anime_id"
    )
    val pictures: List<PicturePersistence>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AnimeGenrePersistence::class,
            parentColumn = "anime_id",
            entityColumn = "genre_id"
        )
    )
    val genres: List<GenrePersistence>
)