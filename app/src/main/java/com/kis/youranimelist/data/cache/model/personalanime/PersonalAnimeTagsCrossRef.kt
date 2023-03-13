package com.kis.youranimelist.data.cache.model.personalanime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "personal_anime_tag",
    foreignKeys = [
        ForeignKey(
            entity = AnimeTagPersistence::class,
            parentColumns = ["tag_id"],
            childColumns = ["tag_id"],
        ),
        ForeignKey(
            entity = AnimePersonalStatusPersistence::class,
            parentColumns = ["anime_id"],
            childColumns = ["anime_id"],
        )
    ],
    primaryKeys = ["anime_id", "tag_id"],
)
data class PersonalAnimeTagsCrossRef(
    @ColumnInfo(name = "anime_id")
    val animeId: Int,
    @ColumnInfo(name = "tag_id")
    val tagId: String,
)
