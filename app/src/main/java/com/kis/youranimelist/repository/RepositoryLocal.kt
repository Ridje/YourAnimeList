package com.kis.youranimelist.repository

import com.kis.youranimelist.model.db.Anime

interface RepositoryLocal {
    fun getUserNote(animeId: Int): Anime?
    fun writeUserNote(animeId: Int, note: String)
}