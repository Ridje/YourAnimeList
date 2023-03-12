package com.kis.youranimelist.data.cache.model.personalanime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence

@Entity(
    tableName = "anime_personal_status",
    foreignKeys = [
        ForeignKey(
            entity = AnimeStatusPersistence::class,
            parentColumns = ["id"],
            childColumns = ["status_id"]
        ),
        ForeignKey(
            entity = AnimePersistence::class,
            parentColumns = ["id"],
            childColumns = ["anime_id"]
        )
    ]
)
data class AnimePersonalStatusPersistence(
    val score: Int?,
    @ColumnInfo(name = "episodes_watched")
    val episodesWatched: Int?,
    @ColumnInfo(name = "status_id", index = true)
    val statusId: String?,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "anime_id", index = true)
    val animeId: Int,
    @ColumnInfo("updated_at")
    val updatedAt: Long,
    @ColumnInfo("comments")
    val comments: String?,
)
