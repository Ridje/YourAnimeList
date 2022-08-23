package com.kis.youranimelist.data.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("genre")
data class GenrePersistence(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
)
