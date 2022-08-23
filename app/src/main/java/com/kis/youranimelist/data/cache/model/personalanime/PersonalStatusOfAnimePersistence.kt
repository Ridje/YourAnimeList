package com.kis.youranimelist.data.cache.model.personalanime

import androidx.room.Embedded
import androidx.room.Relation
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence

data class PersonalStatusOfAnimePersistence(
    @Embedded
    val status: AnimePersonalStatusPersistence,

    @Embedded
    val anime: AnimePersistence,

    @Relation(
        parentColumn = "status_id",
        entityColumn = "id"
    )
    val animeStatusPersistence: AnimeStatusPersistence,

    @Relation(
        parentColumn = "picture_id",
        entityColumn = "id"
    )
    val mainPicture: PicturePersistence,
)
