package com.kis.youranimelist.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kis.youranimelist.model.db.Anime
import com.kis.youranimelist.room.dao.animeDAO

@Database(entities = [Anime::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {

    abstract fun animeDAO(): animeDAO

}