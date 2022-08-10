package com.kis.youranimelist.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kis.youranimelist.data.cache.model.AnimePersistence
import com.kis.youranimelist.data.cache.model.AnimePersonalStatusEntity
import com.kis.youranimelist.data.cache.model.AnimeStatusPersistence

import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO

@Database(
    entities = [AnimePersistence::class, AnimePersonalStatusEntity::class, AnimeStatusPersistence::class],
    version = 3,
    exportSchema = true)
abstract class UserDatabase: RoomDatabase() {
    abstract fun animeDAO(): AnimeDAO
    abstract fun personalAnimeStatusesDAO(): PersonalAnimeDAO
}
