package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeGenrePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RecommendedAnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RelatedAnimePersistence
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnime(animePersistence: AnimePersistence)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnime(animePersistence: List<AnimePersistence>)

    @Query("SELECT * FROM anime")
    fun getAllAnime(): List<AnimePersistence>

    @Query("SELECT EXISTS(SELECT * FROM anime WHERE id = :animeId)")
    fun isAnimeRecordExist(animeId: Int): Boolean

    @Query("SELECT * FROM anime WHERE id = :animeId")
    @Transaction
    fun getAnimeDetailedDataObservable(animeId: Int): Flow<AnimeDetailedDataPersistence?>

    @Query("SELECT * FROM anime WHERE id = :animeId")
    fun getAnimeDetailedData(animeId: Int): AnimePersistence

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRelatedAnime(relatedAnimePersistence: RelatedAnimePersistence)

    @Query("DELETE FROM related_anime WHERE related_anime.anime_id = :animeId")
    fun deleteRelatedAnime(animeId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRecommendedAnime(recommendedAnimePersistence: RecommendedAnimePersistence)

    @Query("DELETE FROM recommended_anime WHERE recommended_anime.anime_id = :animeId")
    fun deleteRecommendedAnime(animeId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnimeGenres(animeGenresPersistence: List<AnimeGenrePersistence>)

    @Query("DELETE FROM anime_genre WHERE anime_id = :animeId")
    fun deleteAnimeGenres(animeId: Int)

    @Transaction
    fun replaceAnimeGenres(animeId: Int, animeGenres: List<AnimeGenrePersistence>) {
        deleteAnimeGenres(animeId)
        addAnimeGenres(animeGenres)
    }
}
