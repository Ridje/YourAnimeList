package com.kis.youranimelist.data.cache.model.anime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "recommended_anime",
    primaryKeys = ["anime_id", "recommended_anime_id"],
    indices = [Index(value = ["recommended_anime_id"])]
)
data class RecommendedAnimePersistence(
    @ColumnInfo("anime_id")
    val animeId: Int,
    @ColumnInfo("recommended_anime_id")
    val recommendedAnime: Int,
    @ColumnInfo("recommended_times")
    val recommendedTimes: Int,
)
