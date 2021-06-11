package com.kis.youranimelist.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kis.youranimelist.model.db.Anime

@Dao
interface AnimeDAO {

    @Query("Select * From Anime WHERE id = :animeId")
    fun getNoteByAnimeID(animeId: Int): Anime?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNoteForAnimeId(anime: Anime)
}