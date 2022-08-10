package com.kis.youranimelist.data.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "anime_personal_status",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = AnimeStatusPersistence::class,
            parentColumns = ["id"],
            childColumns = ["status_id"]),
        ForeignKey(
            entity = AnimePersistence::class,
            parentColumns = ["id"],
            childColumns = ["anime_id"]
        )
    )
)
data class AnimePersonalStatusEntity(
    val score: Int,
    @ColumnInfo(name = "episodes_watched")
    val episodesWatched: Int,
    @ColumnInfo(name = "status_id", index = true)
    val statusId: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "anime_id", index = true)
    val animeId: Int,
)
