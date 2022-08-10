package com.kis.youranimelist.data.cache.model

import androidx.room.Embedded
import androidx.room.Relation

data class AnimeWithPersonalStatusPersistence(
    @Embedded
    val status: AnimePersonalStatusEntity,

    @Relation(
        parentColumn = "anime_id",
        entityColumn = "id"
    )
    val anime: AnimePersistence,

    @Relation(
        parentColumn = "status_id",
        entityColumn = "id"
    )
    val animeStatusPersistence: AnimeStatusPersistence,
)
