package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.AnimePersonalStatusEntity
import com.kis.youranimelist.data.cache.model.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.AnimeWithPersonalStatusPersistence
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalAnimeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeStatus(status: AnimePersonalStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeStatus(statuses: List<AnimePersonalStatusEntity>)

    @Query("DELETE FROM anime_personal_status WHERE anime_id = :animeId")
    fun deletePersonalAnimeStatus(animeId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAnimeStatus(status: AnimeStatusPersistence)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAnimeStatus(statuses: List<AnimeStatusPersistence>)

    @Query("SELECT * FROM anime_personal_status")
    @Transaction
    fun getAllAnimeWithPersonalStatuses(): Flow<List<AnimeWithPersonalStatusPersistence>>

    @Query("Select * FROM anime_personal_status WHERE anime_id = :id")
    @Transaction
    fun getAnimeWithPersonalStatus(id: Int): Flow<AnimeWithPersonalStatusPersistence>
}
