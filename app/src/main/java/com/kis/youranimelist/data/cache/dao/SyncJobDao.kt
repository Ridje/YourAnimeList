package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange

@Dao
interface SyncJobDao {
    @Query("SELECT * FROM deferred_personal_anime_list_change")
    fun getPersonalAnimeListSyncJobs(): List<DeferredPersonalAnimeListChange>

    @Query("SELECT * FROM deferred_personal_anime_list_change WHERE anime_id = :animeId")
    fun getPersonalAnimeListSyncJob(animeId: Int): DeferredPersonalAnimeListChange

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPersonalAnimeListSyncJob(job: DeferredPersonalAnimeListChange)

    @Query("DELETE FROM deferred_personal_anime_list_change WHERE deferred_personal_anime_list_change.anime_id = :animeId")
    fun deletePersonalAnimeListSyncJob(animeId: Int)

    @Query("DELETE FROM deferred_personal_anime_list_change")
    suspend fun deleteAllSyncJobs()

    @Delete
    fun deletePersonalAnimeListSyncJob(jobs: List<DeferredPersonalAnimeListChange>)
}
