package com.kis.youranimelist.data.cache.model.personalanime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tag")
data class AnimeTagPersistence(
    @PrimaryKey
    @ColumnInfo("tag_id")
    val id: String,
)
