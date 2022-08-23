package com.kis.youranimelist.data.cache.model.anime

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity("anime_genre", primaryKeys = ["anime_id", "genre_id"])
data class AnimeGenrePersistence(
    @ColumnInfo("anime_id")
    val animeId: Int,
    @ColumnInfo("genre_id")
    val genreId: Int,
)
