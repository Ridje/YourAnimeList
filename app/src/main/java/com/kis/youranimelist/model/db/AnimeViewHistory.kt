package com.kis.youranimelist.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnimeViewHistory(
    @PrimaryKey
    val animeID: Int,
    val animeName: String,
    val createdAt: Long
    )
