package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kis.youranimelist.data.cache.model.AnimePersistence

@Dao
interface AnimeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnime(animePersistence: AnimePersistence)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnime(animePersistence: List<AnimePersistence>)

    @Query("SELECT * FROM anime")
    fun getAllAnime(): List<AnimePersistence>

    @Query("SELECT * FROM anime WHERE id = :animeId")
    fun getAnimeById(animeId: Int): AnimePersistence
}
