package com.kis.youranimelist.data.cache.localdatasource

import com.kis.youranimelist.core.utils.returnFinishedCatchingWithCancellation
import com.kis.youranimelist.core.utils.runCatchingWithCancellation
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeGenrePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RecommendedAnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RelatedAnimePersistence
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.Genre
import com.kis.youranimelist.domain.rankinglist.model.RecommendedAnime
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AnimeLocalDataSource {
    suspend fun saveRelatedAnime(animeId: Int, relatedAnimeList: List<RelatedAnime>)
    suspend fun saveRecommendedAnime(animeId: Int, recommendedAnimeList: List<RecommendedAnime>)
    suspend fun saveAnimeGenres(animeId: Int, genres: List<Genre>)
    suspend fun saveAnimeToCache(anime: Anime): Boolean

    fun getAnimeDetailedDataProducer(animeId: Int): Flow<AnimeDetailedDataPersistence?>
}

class AnimeLocalDataSourceImpl @Inject constructor(
    private val sideLocalDataSource: SideLocalDataSource,
    private val animeDAO: AnimeDAO,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AnimeLocalDataSource {

    override suspend fun saveAnimeToCache(anime: Anime): Boolean {
        return withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                val startSeasonId = sideLocalDataSource.getOrCreateSeason(
                    anime.startSeason?.year,
                    anime.startSeason?.season
                )?.id

                val mainPictureId = sideLocalDataSource.saveAnimeMainPicture(anime.id,
                    PicturePersistence(
                        0,
                        null,
                        anime.picture?.large,
                        anime.picture?.medium
                    )
                )

                animeDAO.addAnime(
                    AnimePersistence(
                        id = anime.id,
                        title = anime.title,
                        numEpisodes = anime.numEpisodes,
                        synopsis = anime.synopsis,
                        mean = anime.mean,
                        mediaType = anime.mediaType,
                        pictureId = mainPictureId,
                        startSeasonId = startSeasonId,
                        airingStatus = anime.airingStatus
                    )
                )

                saveRelatedAnime(anime.id, anime.relatedAnime)

                saveRecommendedAnime(anime.id, anime.recommendedAnime)

                sideLocalDataSource.saveAnimePictures(
                    animeId = anime.id,
                    pictures = anime.pictures.map {
                        PicturePersistence(
                            0,
                            anime.id,
                            it.large,
                            it.medium,
                        )
                    }
                )

                saveAnimeGenres(anime.id, anime.genres)
            }
        }
    }

    override fun getAnimeDetailedDataProducer(animeId: Int): Flow<AnimeDetailedDataPersistence?> {
        return animeDAO.getAnimeDetailedDataObservable(animeId)
    }

    override suspend fun saveAnimeGenres(animeId: Int, genres: List<Genre>) =
        withContext(ioDispatcher) {
            runCatchingWithCancellation {
                sideLocalDataSource.saveGenres(genres.map { GenrePersistence(it.id, it.name) })
                animeDAO.replaceAnimeGenres(animeId,
                    genres.map { AnimeGenrePersistence(animeId, it.id) })
            }
        }

    override suspend fun saveRelatedAnime(animeId: Int, relatedAnimeList: List<RelatedAnime>) =
        withContext(ioDispatcher) {
            for (relatedAnime in relatedAnimeList) {
                if (!animeDAO.isAnimeRecordExist(relatedAnime.anime.id)) {
                    saveAnimeToCache(relatedAnime.anime)
                }
            }
            animeDAO.deleteRelatedAnime(animeId)

            for (relatedAnime in relatedAnimeList) {
                animeDAO.addRelatedAnime(
                    RelatedAnimePersistence(
                        animeId,
                        relatedAnime.anime.id,
                        relatedAnime.relatedTypeFormatted,
                        relatedAnime.relatedType,
                    )
                )
            }
        }

    override suspend fun saveRecommendedAnime(
        animeId: Int,
        recommendedAnimeList: List<RecommendedAnime>,
    ) =
        withContext(ioDispatcher) {
            for (recommendedAnime in recommendedAnimeList) {
                if (!animeDAO.isAnimeRecordExist(recommendedAnime.anime.id)) {
                    saveAnimeToCache(recommendedAnime.anime)
                }
            }
            animeDAO.deleteRecommendedAnime(animeId)
            for (recommendedAnime in recommendedAnimeList) {
                animeDAO.addRecommendedAnime(
                    RecommendedAnimePersistence(
                        animeId,
                        recommendedAnime.anime.id,
                        recommendedAnime.recommendedTimes
                    )
                )
            }
        }
}
