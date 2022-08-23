package com.kis.youranimelist.data.cache.model.personalanime

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_status")
data class AnimeStatusPersistence(
    @PrimaryKey(autoGenerate = false)
    val id: String,
)
