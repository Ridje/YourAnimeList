package com.kis.youranimelist.data.cache.model.anime

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "related_anime", primaryKeys = ["anime_id", "related_anime_id"])
data class RelatedAnimePersistence(
    @ColumnInfo("anime_id")
    val animeId: Int,
    @ColumnInfo("related_anime_id")
    val relatedAnime: Int,
    @ColumnInfo("related_type_formatted")
    val relatedTypeFormatted: String,
    @ColumnInfo("related_type")
    val relatedType: String,
)
