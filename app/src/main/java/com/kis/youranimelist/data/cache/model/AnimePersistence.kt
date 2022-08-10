package com.kis.youranimelist.data.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime")
data class AnimePersistence(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @ColumnInfo(name = "num_episodes")
    val numEpisodes: Int,
    val mean: Float,
    @ColumnInfo(name = "media_type")
    val mediaType: String,
    @ColumnInfo(name = "picture_link")
    val pictureLink: String,
)
