package com.kis.youranimelist.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SideDAO
import com.kis.youranimelist.data.cache.dao.UserDAO
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeGenrePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RelatedAnimePersistence
import com.kis.youranimelist.data.cache.model.anime.SeasonPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence

@Database(
    entities = [
        AnimePersistence::class,
        AnimePersonalStatusPersistence::class,
        AnimeStatusPersistence::class,
        UserPersistence::class,
        PicturePersistence::class,
        SeasonPersistence::class,
        GenrePersistence::class,
        RelatedAnimePersistence::class,
        AnimeGenrePersistence::class,
    ],
    version = 3,
    exportSchema = true)
abstract class UserDatabase : RoomDatabase() {
    abstract fun animeDAO(): AnimeDAO
    abstract fun personalAnimeStatusesDAO(): PersonalAnimeDAO
    abstract fun userDAO(): UserDAO
    abstract fun sideDAO(): SideDAO
}
