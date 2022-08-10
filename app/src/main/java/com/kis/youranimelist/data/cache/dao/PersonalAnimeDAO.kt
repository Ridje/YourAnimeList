package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.AnimePersonalStatusEntity
import com.kis.youranimelist.data.cache.model.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.AnimeWithPersonalStatusPersistence

@Dao
interface PersonalAnimeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeStatus(status: AnimePersonalStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeStatus(statuses: List<AnimePersonalStatusEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAnimeStatus(status: AnimeStatusPersistence)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAnimeStatus(statuses: List<AnimeStatusPersistence>)

    @Query("SELECT * FROM anime_personal_status")
    @Transaction
    fun getAllAnimeWithPersonalStatuses(): List<AnimeWithPersonalStatusPersistence>
}
