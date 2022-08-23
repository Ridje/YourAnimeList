package com.kis.youranimelist.data.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence

@Entity("picture",
    foreignKeys = [
        ForeignKey(
            entity = AnimePersistence::class,
            parentColumns = ["id"],
            childColumns = ["anime_id"]),
    ]
)
data class PicturePersistence(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo("anime_id")
    val animeId: Int?,
    val large: String?,
    val medium: String?,
)
