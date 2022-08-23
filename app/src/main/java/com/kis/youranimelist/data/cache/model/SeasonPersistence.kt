package com.kis.youranimelist.data.cache.model.anime

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("season")
data class SeasonPersistence (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val year: Int?,
    val season: String?,
)
