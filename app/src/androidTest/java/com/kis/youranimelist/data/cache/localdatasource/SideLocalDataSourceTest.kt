package com.kis.youranimelist.data.cache.localdatasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.SideDAO
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SideLocalDataSourceTest {
    @get:Rule(order = 0)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var sideLocalDataSource: SideLocalDataSource

    // to check results
    @Inject
    lateinit var sideDAO: SideDAO

    @Inject
    lateinit var animeDAO: AnimeDAO

    private val genreAction = GenrePersistence(
        1,
        "Action"
    )
    private val genreHorror = GenrePersistence(
        2,
        "Horror"
    )
    private val pictureMain = PicturePersistence(
        0,
        null,
        "link_1",
        "link_2",
    )
    private val genreHorrorDuplicate = genreHorror.copy(name = "Not horror")
    private val genres = listOf(genreAction, genreHorror)
    private val someAnime = AnimePersistence(
        id = 1,
        title = "Some title",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )
    private val sidePicturesOfSomeAnime = listOf(
        PicturePersistence(
            0,
            someAnime.id,
            "large_link_2",
            "small_link_2"
        ),
        PicturePersistence(
            0,
            someAnime.id,
            "large_link_2",
            "small_link_2"
        )
    )

    private val seasonYear = 1994
    private val seasonSeason = "spring"

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun saveGenresShouldBeSucceeded() = runTest {
        assertThat(sideDAO.getGenres()).isEmpty()
        sideLocalDataSource.saveGenres(genres)
        assertThat(sideDAO.getGenres()).hasSize(genres.size)
    }

    @Test
    fun saveGenresShouldSaveOnlyUniqueRowsWithGroupByLastValue() = runTest {
        assertThat(sideDAO.getGenres()).isEmpty()
        sideLocalDataSource.saveGenres(genres.toMutableList().apply { add(genreHorrorDuplicate) })
        val genresAfterInsert = sideDAO.getGenres()
        assertThat(genresAfterInsert).hasSize(genres.size)
        assertThat(genresAfterInsert).isEqualTo(listOf(genreAction, genreHorrorDuplicate))
    }

    @Test
    fun saveAnimeMainPictureShouldGenerateNewIdIfZeroAndAnimeDoesNotHaveMainPicture() = runTest {
        val newPictureId = sideLocalDataSource.saveAnimeMainPicture(0, pictureMain)
        assertThat(newPictureId).isNotNull()
        assertThat(newPictureId).isAtLeast(1)
        val insertedValue = sideDAO.getPicture(newPictureId!!)
        assertThat(insertedValue).isNotNull()
        assertThat(insertedValue?.animeId).isNull()
    }

    @Test
    fun saveAnimeMainPictureWithExistedMainPictureShouldReplaceIt() = runTest {
        sideLocalDataSource.saveAnimeMainPicture(someAnime.id, pictureMain.copy(id = 1))
        animeDAO.addAnime(someAnime.copy(pictureId = 1))
        sideLocalDataSource.saveAnimeMainPicture(someAnime.id,
            pictureMain.copy(id = 10, large = "link_3"))
        assertThat(sideDAO.getAnimeMainPictureByAnimeId(someAnime.id)?.id ?: 0).isEqualTo(1)
        assertThat(sideDAO.getAnimeMainPictureByAnimeId(someAnime.id)?.large
            ?: "returned nothing").isEqualTo("link_3")
    }


    @Test
    fun saveAnimeNullMainPictureDeleteCurrentMainPicture() = runTest {
        sideLocalDataSource.saveAnimeMainPicture(someAnime.id, pictureMain.copy(id = 1))
        animeDAO.addAnime(someAnime.copy(pictureId = 1))
        sideLocalDataSource.saveAnimeMainPicture(someAnime.id,
            PicturePersistence(0, null, null, null))
        assertThat(sideDAO.getAnimeMainPictureByAnimeId(someAnime.id)).isNull()
    }

    @Test
    fun saveAnimePicturesAddsNewPictures() = runTest {
        animeDAO.addAnime(someAnime)
        sideLocalDataSource.saveAnimePictures(someAnime.id, sidePicturesOfSomeAnime)
        val insertedPictures = sideDAO.getPicturesByAnimeId(someAnime.id)
        assertThat(insertedPictures).hasSize(sidePicturesOfSomeAnime.size)
        sidePicturesOfSomeAnime.forEachIndexed { index, picturePersistence ->
            assertThat(picturePersistence.large).isEqualTo(insertedPictures[index].large)
            assertThat(picturePersistence.medium).isEqualTo(insertedPictures[index].medium)
            assertThat(picturePersistence.animeId).isEqualTo(insertedPictures[index].animeId)
        }
    }

    @Test
    fun saveAnimePicturesAddsNewForReferencedAnimeAndIgnoresPicturePersistenceAnimeId() = runTest {
        animeDAO.addAnime(someAnime)
        sideLocalDataSource.saveAnimePictures(someAnime.id,
            sidePicturesOfSomeAnime.toMutableList().apply {
                add(
                    PicturePersistence(
                        0,
                        10,
                        null,
                        null,
                    )
                )
            })
        val insertedPictures = sideDAO.getPicturesByAnimeId(someAnime.id)
        assertThat(insertedPictures).hasSize(sidePicturesOfSomeAnime.size + 1)
    }

    @Test
    fun saveAnimePicturesEraseOldPictures() = runTest {
        animeDAO.addAnime(someAnime)
        val idOldPicture = sideDAO.addPicture(
            PicturePersistence(
                0,
                someAnime.id,
                "large_link_2",
                "small_link_2"
            ))
        sideLocalDataSource.saveAnimePictures(someAnime.id, sidePicturesOfSomeAnime)
        assertThat(sideDAO.getPicture(idOldPicture)).isNull()
    }

    @Test
    fun getOrCreateSeasonCreateSeasonIfNotExist() = runTest {
        assertThat(sideDAO.getSeasonByYearAndSeason(seasonYear, seasonSeason)).isNull()
        val season = sideLocalDataSource.getOrCreateSeason(seasonYear, seasonSeason)
        assertThat(sideDAO.getSeasonByYearAndSeason(seasonYear, seasonSeason)).isEqualTo(season)
    }

    @Test
    fun getOrCreateSeasonReturnExistingSeasonIfExist() = runTest {
        val insertedFirstSeason = sideLocalDataSource.getOrCreateSeason(seasonYear, seasonSeason)
        assertThat(sideDAO.getSeasonByYearAndSeason(seasonYear, seasonSeason)).isNotNull()
        sideLocalDataSource.getOrCreateSeason(seasonYear, seasonSeason)
        assertThat(sideDAO.getSeasonByYearAndSeason(seasonYear, seasonSeason)).isEqualTo(
            insertedFirstSeason)
    }
}
