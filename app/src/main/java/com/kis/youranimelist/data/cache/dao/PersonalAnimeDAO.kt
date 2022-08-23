package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalAnimeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeStatus(status: AnimePersonalStatusPersistence)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeStatus(statuses: List<AnimePersonalStatusPersistence>)

    @Query("DELETE FROM anime_personal_status WHERE anime_id = :animeId")
    fun deletePersonalAnimeStatus(animeId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAnimeStatus(status: AnimeStatusPersistence)

    @Query("SELECT * FROM anime_personal_status INNER JOIN anime ON anime_personal_status.anime_id = anime.id")
    @Transaction
    fun getAllAnimeWithPersonalStatuses(): Flow<List<PersonalStatusOfAnimePersistence>>

    @Query("Select * FROM anime LEFT JOIN anime_personal_status ON anime_personal_status.anime_id = anime.id WHERE anime.id = :id")
    @Transaction
    fun getAnimeWithPersonalStatus(id: Int): Flow<PersonalStatusOfAnimePersistence>
}
