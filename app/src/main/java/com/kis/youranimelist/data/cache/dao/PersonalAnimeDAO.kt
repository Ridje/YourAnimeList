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
    suspend fun addPersonalAnimeStatus(status: AnimePersonalStatusPersistence)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeStatus(statuses: List<AnimePersonalStatusPersistence>)

    @Transaction
    suspend fun mergeAnimePersonalStatus(newStatus: AnimePersonalStatusPersistence) {
        if ((getAnimePersonalStatus(newStatus.animeId)?.updatedAt ?: 0) < newStatus.updatedAt) {
            addPersonalAnimeStatus(newStatus)
        }
    }

    @Query("SELECT * FROM anime_personal_status WHERE anime_id = :animeId")
    fun getAnimePersonalStatus(animeId: Int): AnimePersonalStatusPersistence?

    @Query("DELETE FROM anime_personal_status WHERE anime_id = :animeId")
    suspend fun deletePersonalAnimeStatus(animeId: Int)

    @Query("DELETE FROM anime_personal_status")
    fun deleteAllPersonalStatuses()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAnimeStatus(status: AnimeStatusPersistence)

    @Query("SELECT * FROM anime_personal_status INNER JOIN anime ON anime_personal_status.anime_id = anime.id")
    @Transaction
    fun getAllAnimeWithPersonalStatuses(): Flow<List<PersonalStatusOfAnimePersistence>>

    @Query("Select * FROM anime LEFT JOIN anime_personal_status ON anime_personal_status.anime_id = anime.id WHERE anime.id = :id")
    @Transaction
    fun getAnimeWithPersonalStatus(id: Int): Flow<PersonalStatusOfAnimePersistence>
}
