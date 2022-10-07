package com.kis.youranimelist.data.cache.localdatasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.Genre
import com.kis.youranimelist.domain.rankinglist.model.Picture
import com.kis.youranimelist.domain.rankinglist.model.RecommendedAnime
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import com.kis.youranimelist.domain.rankinglist.model.Season
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class AnimeLocalDataSourceTest {

    @get:Rule(order = 0)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var animeLocalDataSource: AnimeLocalDataSource

    @Inject
    lateinit var animeMapper: AnimeMapper

    @Inject
    lateinit var animeDAO: AnimeDAO

    private val someAnime = Anime(
        id = 1,
        title = "Some title"
    )

    private val someAnimeExpectedResult = AnimeDetailedDataPersistence(
        anime = AnimePersistence(
            id = someAnime.id,
            title = someAnime.title,
            numEpisodes = someAnime.numEpisodes,
            synopsis = someAnime.synopsis,
            mean = someAnime.mean,
            mediaType = someAnime.mediaType,
            pictureId = null,
            startSeasonId = null,
            airingStatus = someAnime.airingStatus,
        ),
        relatedAnime = listOf(),
        relatedAnimeAdditionValues = listOf(),
        recommendedAnime = listOf(),
        recommendedAnimeAdditionValues = listOf(),
        mainPicture = null,
        startSeason = null,
        pictures = listOf(),
        genres = listOf(),
        status = null,
    )

    private val someAnimeUpdate = Anime(
        id = 1,
        title = "Some title",
        picture = Picture(
            "line1",
            "line2"
        ),
        startSeason = Season(
            1994, "spring"
        ),
        mean = 5.5f,
        synopsis = "MAL",
        genres = listOf(Genre(1, "Action"), Genre(2, "Horror")),
        pictures = listOf(Picture("picture1", "picture1medium"),
            Picture("picture2", "picture2medium")),
        relatedAnime = listOf(
            RelatedAnime(Anime(id = 2, title = "2"), "related", "related"),
            RelatedAnime(Anime(id = 3, title = "3"), "related", "related"),
        ),
        recommendedAnime = listOf(
            RecommendedAnime(Anime(id = 4, title = "4"), 5),
            RecommendedAnime(Anime(id = 5, title = "5"), 7),
        ),
        rank = 100,
        mediaType = "TV",
        numEpisodes = 10,
        airingStatus = "finished",
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun addNewAnimeAllFieldsAreNullToCache() = runTest {
        val animeFlow = animeLocalDataSource.getAnimeDetailedDataProducer(someAnime.id)
        animeLocalDataSource.saveAnimeToCache(someAnime)
        val result = animeFlow.first()
        assertThat(someAnimeExpectedResult).isEqualTo(result)
    }

    @Test
    fun updateAnimeAllFieldsAreChanging() = runTest {
        val animeFlow = animeLocalDataSource.getAnimeDetailedDataProducer(someAnime.id)
        animeLocalDataSource.saveAnimeToCache(someAnime)
        animeLocalDataSource.saveAnimeToCache(someAnimeUpdate)
        val resultAnime = animeFlow.first()
        assertThat(resultAnime).isNotNull()
        val mappedValue = animeMapper.map(resultAnime!!)
        assertThat(mappedValue).isEqualTo(someAnimeUpdate.copy(
            // Because we don't keep rank yet
            rank = null)
        )
    }

    @Test
    fun saveAnimeGenres() = runTest {
        animeDAO.addAnime(AnimePersistence(1, "Title", null, null, null, null, null, null, null))
        animeLocalDataSource.saveAnimeGenres(1, listOf())
        assertThat(animeLocalDataSource.getAnimeDetailedDataProducer(1).first()?.genres).isEmpty()
        animeLocalDataSource.saveAnimeGenres(1, listOf(Genre(1, "Action")))
        assertThat(animeLocalDataSource.getAnimeDetailedDataProducer(1).first()?.genres).hasSize(1)
        animeLocalDataSource.saveAnimeGenres(1, listOf())
        assertThat(animeLocalDataSource.getAnimeDetailedDataProducer(1).first()?.genres).isEmpty()
    }

    @Test
    fun saveRelatedAnimeCreatesAnimeIfNotExist() = runTest {
        animeLocalDataSource.saveAnimeToCache(Anime(1, "1"))
        animeLocalDataSource.saveRelatedAnime(
            1,
            listOf(
                RelatedAnime(
                    anime = Anime(
                        2,
                        "2"
                    ),
                    relatedType = "",
                    relatedTypeFormatted = ""
                )
            )
        )
        assertThat(animeLocalDataSource.getAnimeDetailedDataProducer(1)
            .first()?.relatedAnime).hasSize(1)
        animeLocalDataSource.saveRelatedAnime(1, listOf())
        assertThat(animeLocalDataSource.getAnimeDetailedDataProducer(1)
            .first()?.relatedAnime).isEmpty()
    }

    @Test
    fun saveRecommendedAnimeCreatesAnimeIfNotExist() = runTest {
        animeLocalDataSource.saveAnimeToCache(Anime(1, "1"))
        animeLocalDataSource.saveRecommendedAnime(1,
            listOf(
                RecommendedAnime(
                    Anime(1, "1"),
                    5
                )
            )
        )
        assertThat(animeLocalDataSource.getAnimeDetailedDataProducer(1)
            .first()?.recommendedAnime).hasSize(1)
        animeLocalDataSource.saveRecommendedAnime(1, listOf())
        assertThat(animeLocalDataSource.getAnimeDetailedDataProducer(1)
            .first()?.recommendedAnime).isEmpty()
    }
}
