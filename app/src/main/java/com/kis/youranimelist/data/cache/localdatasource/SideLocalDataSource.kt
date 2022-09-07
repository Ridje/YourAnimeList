package com.kis.youranimelist.data.cache.localdatasource

import com.kis.youranimelist.core.utils.runCatchingWithCancellation
import com.kis.youranimelist.data.cache.dao.SideDAO
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.SeasonPersistence
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SideLocalDataSource {
    suspend fun saveGenres(genres: List<GenrePersistence>)
    suspend fun saveAnimePictures(animeId: Int, pictures: List<PicturePersistence>)
    suspend fun saveAnimeMainPicture(animeId: Int, picture: PicturePersistence): Long?
    suspend fun getPictureById(pictureId: Long): PicturePersistence?
    suspend fun getOrCreateSeason(year: Int?, season: String?): SeasonPersistence?
}

class SideLocalDataSourceImpl @Inject constructor(
    private val sideDAO: SideDAO,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SideLocalDataSource {
    override suspend fun saveGenres(genres: List<GenrePersistence>) {
        withContext(ioDispatcher) {
            runCatchingWithCancellation { sideDAO.addGenres(genres) }
        }
    }

    override suspend fun saveAnimePictures(animeId: Int, pictures: List<PicturePersistence>) {
        withContext(ioDispatcher) {
            runCatchingWithCancellation {
                sideDAO.replaceAnimePictures(
                    animeId,
                    pictures.map { it.copy(animeId = animeId) }
                )
            }
        }
    }

    override suspend fun saveAnimeMainPicture(animeId: Int, picture: PicturePersistence): Long? {
        return withContext(ioDispatcher) {
            runCatchingWithCancellation {
                sideDAO.addPicture(
                    picture.copy(
                        id = sideDAO.getAnimeMainPictureByAnimeId(animeId)?.id ?: 0
                    )
                )
            }
        }
    }

    override suspend fun getPictureById(pictureId: Long): PicturePersistence? {
        return withContext(ioDispatcher) {
            runCatchingWithCancellation {
                sideDAO.getPicture(pictureId = pictureId)
            }
        }
    }

    override suspend fun getOrCreateSeason(year: Int?, season: String?): SeasonPersistence? {
        return withContext(ioDispatcher) {
            return@withContext runCatchingWithCancellation {
                sideDAO.getSeasonByYearAndSeason(year, season) ?: run {
                    val newSeason = SeasonPersistence(0, year, season)
                    val newSeasonId = sideDAO.addSeason(newSeason)
                    newSeason.copy(id = newSeasonId)
                }
            }
        }
    }
}
