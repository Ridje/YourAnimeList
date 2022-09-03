package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange

@Dao
interface SyncJobDao {
    @Query("SELECT * FROM deferred_personal_anime_list_change")
    fun getPersonalAnimeListSyncJobs(): List<DeferredPersonalAnimeListChange>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalAnimeListSyncJob(job: DeferredPersonalAnimeListChange)

    @Delete
    fun deletePersonalAnimeListSyncJob(job: DeferredPersonalAnimeListChange)

    @Query("DELETE FROM deferred_personal_anime_list_change WHERE deferred_personal_anime_list_change.anime_id = :animeId")
    fun deletePersonalAnimeListSyncJob(animeId: Int)

    @Query("DELETE FROM deferred_personal_anime_list_change")
    fun deleteAllSyncJobs()

    @Transaction
    fun deletePersonalAnimeListSyncJob(jobs: List<DeferredPersonalAnimeListChange>) {
        for (job in jobs) {
            deletePersonalAnimeListSyncJob(job)
        }
    }
}
