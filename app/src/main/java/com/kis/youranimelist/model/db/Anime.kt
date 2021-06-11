package com.kis.youranimelist.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Anime(
    @PrimaryKey
    val id: Int,
    val userNote: String = ""
)
