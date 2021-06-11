package com.kis.youranimelist.repository

import com.kis.youranimelist.model.db.Anime
import com.kis.youranimelist.model.db.AnimeViewHistory

interface RepositoryLocal {
    fun getUserNote(animeId: Int): Anime?
    fun writeUserNote(animeId: Int, note: String)

    fun getAnimeViewHistory(): List<AnimeViewHistory>
    fun addAnimeViewHistory(animeId: Int, animeName: String)
}