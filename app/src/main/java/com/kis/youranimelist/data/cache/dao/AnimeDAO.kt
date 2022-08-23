package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeGenrePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RelatedAnimePersistence
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnime(animePersistence: AnimePersistence)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnime(animePersistence: List<AnimePersistence>)

    @Query("SELECT * FROM anime")
    fun getAllAnime(): List<AnimePersistence>

    @Query("SELECT * FROM anime WHERE id = :animeId")
    fun getAnimeByIdObservable(animeId: Int): Flow<AnimeDetailedDataPersistence?>

    @Query("SELECT EXISTS(SELECT * FROM anime WHERE id = :animeId)")
    fun isAnimeRecordExist(animeId: Int): Boolean

    @Query("SELECT * FROM anime WHERE id = :animeId")
    fun getAnimeDetailedDataObservable(animeId: Int): Flow<AnimeDetailedDataPersistence>

    @Query("SELECT * FROM anime WHERE id = :animeId")
    fun getAnimeDetailedData(animeId: Int): AnimePersistence

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRelatedAnime(relatedAnimePersistence: RelatedAnimePersistence)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnimeGenre(animeGenrePersistence: AnimeGenrePersistence)

}
