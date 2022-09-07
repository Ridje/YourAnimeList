package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.SeasonPersistence

@Dao
interface SideDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addGenres(genres: List<GenrePersistence>)

    @Query("SELECT * FROM genre")
    fun getGenres(): List<GenrePersistence>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPicture(picturePersistence: PicturePersistence): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPicture(picturePersistence: List<PicturePersistence>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSeason(seasonPersistence: SeasonPersistence): Long

    @Query("SELECT * FROM picture WHERE id = :pictureId")
    fun getPicture(pictureId: Long): PicturePersistence?

    @Query("SELECT picture.* FROM picture INNER JOIN anime ON anime.picture_id = picture.id WHERE anime.id = :animeId")
    fun getAnimeMainPictureByAnimeId(animeId: Int): PicturePersistence?

    @Query("SELECT * FROM season WHERE year = :year AND season = :season")
    fun getSeasonByYearAndSeason(year: Int?, season: String?): SeasonPersistence?

    @Query("DELETE FROM picture WHERE anime_id is NOT NULL AND anime_id = :animeId")
    fun deleteAnimePicturesByAnimeId(animeId: Int)

    @Query("SELECT * FROM picture WHERE anime_id is NOT NULL and anime_id = :animeId")
    fun getPicturesByAnimeId(animeId: Int): List<PicturePersistence>

    @Transaction
    fun replaceAnimePictures(animeId: Int, pictures: List<PicturePersistence>) {
        deleteAnimePicturesByAnimeId(animeId)
        addPicture(pictures)
    }
}
