package com.kis.youranimelist.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kis.youranimelist.model.db.Anime
import com.kis.youranimelist.model.db.AnimeViewHistory

import com.kis.youranimelist.room.dao.AnimeDAO
import com.kis.youranimelist.room.dao.AnimeViewHistoryDAO

@Database(
    entities = [Anime::class, AnimeViewHistory::class],
    version = 3,
    exportSchema = true)
abstract class UserDatabase: RoomDatabase() {

    abstract fun animeDAO(): AnimeDAO

    abstract fun animeViewHistory(): AnimeViewHistoryDAO
}