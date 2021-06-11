package com.kis.youranimelist.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kis.youranimelist.model.db.AnimeViewHistory

@Dao
interface AnimeViewHistoryDAO {

    @Query("SELECT * FROM AnimeViewHistory ORDER BY createdAt DESC")
    fun getAnimeViewHistory(): List<AnimeViewHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimeToViewHistory(animeViewHistory: AnimeViewHistory)
}