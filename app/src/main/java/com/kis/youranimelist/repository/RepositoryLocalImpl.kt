package com.kis.youranimelist.repository

import com.kis.youranimelist.model.db.Anime
import com.kis.youranimelist.model.db.AnimeViewHistory
import com.kis.youranimelist.room.UserDatabase

class RepositoryLocalImpl(val service: UserDatabase) : RepositoryLocal {

    override fun getUserNote(animeId: Int): Anime? {
        return service.animeDAO().getNoteByAnimeID(animeId)
    }

    override fun writeUserNote(animeId: Int, note: String) {
        service.animeDAO().addNoteForAnimeId(Anime(animeId, note))
    }

    override fun getAnimeViewHistory(): List<AnimeViewHistory> {
        return service.animeViewHistory().getAnimeViewHistory()
    }


    override fun addAnimeViewHistory(animeId: Int, animeName: String) {
        service.animeViewHistory().insertAnimeToViewHistory(AnimeViewHistory(animeId, animeName, System.currentTimeMillis()))
    }
}