package com.kis.youranimelist.data.cache.model.anime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kis.youranimelist.data.cache.model.PicturePersistence

@Entity(tableName = "anime",
    foreignKeys = [
        ForeignKey(
            entity = PicturePersistence::class,
            parentColumns = ["id"],
            childColumns = ["picture_id"]
        ),
        ForeignKey(
            entity = SeasonPersistence::class,
            parentColumns = ["id"],
            childColumns = ["start_season_id"]
        )
    ]
)
data class AnimePersistence(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    @ColumnInfo(name = "num_episodes")
    val numEpisodes: Int?,
    val synopsis: String?,
    val mean: Float?,
    @ColumnInfo(name = "media_type")
    val mediaType: String?,
    @ColumnInfo(name = "picture_id")
    val pictureId: Long?,
    @ColumnInfo("start_season_id")
    val startSeasonId: Long?,
)
