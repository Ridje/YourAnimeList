package com.kis.youranimelist.data.cache.localdatasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SyncJobDao
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue
import com.kis.youranimelist.domain.rankinglist.model.Anime
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
class PersonalAnimeLocalDataSourceTest {


    @get:Rule(order = 0)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var personalAnimeLocalDataSource: PersonalAnimeLocalDataSource

    @Inject
    lateinit var animeDAO: AnimeDAO

    @Inject
    lateinit var personalAnimeDAO: PersonalAnimeDAO

    @Inject
    lateinit var syncJobDAO: SyncJobDao

    private val animeStatus = AnimeStatus(
        anime = Anime(
            id = 1,
            title = "Some title"
        ),
        status = AnimeStatusValue.Watching,
        score = 10,
        numWatchedEpisodes = 10,
        updatedAt = System.currentTimeMillis(),
    )

    private val animeStatusNewValues = animeStatus.copy(
        status = AnimeStatusValue.Dropped,
        score = 1,
    )

    private val anotherAnimeStatusNext = AnimeStatus(
        anime = Anime(
            id = 2,
            title = "Some another title"
        ),
        status = AnimeStatusValue.Completed,
        score = 10,
        numWatchedEpisodes = 5,
        updatedAt = System.currentTimeMillis()
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun emptyOneAnimeProducerShouldReturnNull() = runTest {
        val dataSource =
            personalAnimeLocalDataSource.getAnimeWithStatusProducer(animeStatus.anime.id)
        val result = dataSource.first()
        assertThat(result).isNull()
    }

    @Test
    fun emptyMultiplyAnimeProducerShouldReturnEmptyList() = runTest {
        val dataSource = personalAnimeLocalDataSource.getAnimeWithStatusProducer()
        val result = dataSource.first()
        assertThat(result).isNotNull()
        assertThat(result).isEmpty()
    }

    @Test
    fun saveAnimeWithPersonalStatus() = runTest {
        val dataSource =
            personalAnimeLocalDataSource.getAnimeWithStatusProducer(animeStatus.anime.id)
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(animeStatus)
        val result = dataSource.first()
        assertThat(result).isNotNull()
    }

    @Test
    fun saveAnimeListWithPersonalStatuses() = runTest {
        val dataSource = personalAnimeLocalDataSource.getAnimeWithStatusProducer()
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(
            listOf(
                animeStatus,
                anotherAnimeStatusNext,
            )
        )
        val result = dataSource.first()
        assertThat(result).isNotNull()
        assertThat(result).hasSize(2)
    }

    @Test
    fun saveTwiceAnimePersonalStatusChangesFirstValue() = runTest {
        val dataSource =
            personalAnimeLocalDataSource.getAnimeWithStatusProducer(animeStatus.anime.id)
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(animeStatus)
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(animeStatusNewValues)
        val result = dataSource.first()
        assertThat(result).isNotNull()
        assertThat(result?.status?.score).isEqualTo(animeStatusNewValues.score)
        assertThat(result?.animeStatusPersistence?.id).isEqualTo(animeStatusNewValues.status.presentIndex)
    }

    @Test
    fun mergeStatusesSaveStatusOnFirstInsertion() = runTest {
        val personalStatus = AnimeStatusValue.Dropped.presentIndex
        animeDAO.addAnime(
            AnimePersistence(
                id = 1,
                title = "test",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        )
        personalAnimeDAO.addAnimeStatus(
            AnimeStatusPersistence(
                personalStatus
            )
        )
        val animeToInsert = AnimePersonalStatusPersistence(
            1,
            5,
            personalStatus,
            1,
            System.currentTimeMillis()
        )
        personalAnimeLocalDataSource.mergePersonalAnimeStatus(animeToInsert)
        val result = personalAnimeDAO.getAnimePersonalStatus(1)
        assertThat(result).isEqualTo(animeToInsert)
    }

    @Test
    fun mergeStatusesNotSaveStatusInCaseOfEarlierUpdateData() = runTest {
        val personalStatus = AnimeStatusValue.Dropped.presentIndex
        animeDAO.addAnime(
            AnimePersistence(
                id = 1,
                title = "test",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        )
        personalAnimeDAO.addAnimeStatus(
            AnimeStatusPersistence(
                personalStatus
            )
        )
        val animeToInsert = AnimePersonalStatusPersistence(
            1,
            5,
            personalStatus,
            1,
            System.currentTimeMillis()
        )
        personalAnimeLocalDataSource.mergePersonalAnimeStatus(animeToInsert)
        personalAnimeLocalDataSource.mergePersonalAnimeStatus(
            animeToInsert.copy(updatedAt = animeToInsert.updatedAt - 10000L)
        )
        val result = personalAnimeDAO.getAnimePersonalStatus(1)
        assertThat(result).isEqualTo(animeToInsert)
    }

    @Test
    fun mergeStatusesSaveStatusInCaseOfLaterUpdateData() = runTest {
        val personalStatus = AnimeStatusValue.Dropped.presentIndex
        animeDAO.addAnime(
            AnimePersistence(
                id = 1,
                title = "test",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        )
        personalAnimeDAO.addAnimeStatus(
            AnimeStatusPersistence(
                personalStatus
            )
        )
        val animeToInsert = AnimePersonalStatusPersistence(
            1,
            5,
            personalStatus,
            1,
            System.currentTimeMillis()
        )
        personalAnimeLocalDataSource.mergePersonalAnimeStatus(animeToInsert)
        personalAnimeLocalDataSource.mergePersonalAnimeStatus(
            animeToInsert.copy(updatedAt = animeToInsert.updatedAt + 10000L)
        )
        val result = personalAnimeDAO.getAnimePersonalStatus(1)
        assertThat(result).isEqualTo(animeToInsert.copy(updatedAt = animeToInsert.updatedAt + 10000L))
    }

    @Test
    fun mergeStatusesNotSaveStatusInCaseOfEqualUpdateData() = runTest {
        val personalStatus = AnimeStatusValue.Dropped.presentIndex
        animeDAO.addAnime(
            AnimePersistence(
                id = 1,
                title = "test",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        )
        personalAnimeDAO.addAnimeStatus(
            AnimeStatusPersistence(
                personalStatus
            )
        )
        val animeToInsert = AnimePersonalStatusPersistence(
            1,
            5,
            personalStatus,
            1,
            System.currentTimeMillis()
        )
        personalAnimeLocalDataSource.mergePersonalAnimeStatus(animeToInsert)
        personalAnimeLocalDataSource.mergePersonalAnimeStatus(
            animeToInsert.copy(score = 3)
        )
        val result = personalAnimeDAO.getAnimePersonalStatus(1)
        assertThat(result).isEqualTo(animeToInsert)
    }

    @Test
    fun deleteEmptyStatusesShouldNotThrowErrors() = runTest {
        personalAnimeLocalDataSource.deleteAnimePersonalStatus(animeId = 1)
        personalAnimeLocalDataSource.deleteAllPersonalStatuses()
    }

    @Test
    fun deletePersonalStatusShouldWork() = runTest {
        val dataSource =
            personalAnimeLocalDataSource.getAnimeWithStatusProducer(animeStatus.anime.id)
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(animeStatus)
        personalAnimeLocalDataSource.deleteAnimePersonalStatus(animeStatus.anime.id)
        assertThat(dataSource.first()?.status).isNull()
    }

    @Test
    fun deleteAllPersonalStatusesShouldWork() = runTest {
        val dataSource =
            personalAnimeLocalDataSource.getAnimeWithStatusProducer(animeStatus.anime.id)
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(animeStatus)
        personalAnimeLocalDataSource.deleteAllPersonalStatuses()
        assertThat(dataSource.first()?.status).isNull()
    }

    @Test
    fun savePersonalAnimeStatusToCacheShouldNotAddSyncWork() = runTest {
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(animeStatus)
        assertThat(syncJobDAO.getPersonalAnimeListSyncJob(animeId = animeStatus.anime.id)).isNull()
    }

    @Test
    fun savePersonalAnimeStatusesToCacheShouldNotAddSyncWork() = runTest {
        personalAnimeLocalDataSource.saveAnimeWithPersonalStatus(listOf(animeStatus))
        assertThat(syncJobDAO.getPersonalAnimeListSyncJob(animeId = animeStatus.anime.id)).isNull()
    }

    @Test
    fun deleteAllPersonalStatusesShouldNotDeleteOrAddSyncWork() = runTest {
        animeDAO.addAnime(
            AnimePersistence(
                id = 1,
                title = "test",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        )
        syncJobDAO.addPersonalAnimeListSyncJob(
            DeferredPersonalAnimeListChange(
                1,
                false,
                System.currentTimeMillis()
            )
        )
        personalAnimeLocalDataSource.deleteAllPersonalStatuses()
        val asyncJob = syncJobDAO.getPersonalAnimeListSyncJob(animeId = 1)
        assertThat(asyncJob).isNotNull()
        assertThat(asyncJob.deleted).isFalse()
    }

    @Test
    fun saveAnimePersonalStatusPersistenceShouldAddChangeSyncWork() = runTest {
        val personalStatus = AnimeStatusValue.Dropped.presentIndex
        animeDAO.addAnime(
            AnimePersistence(
                id = 1,
                title = "test",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        )
        personalAnimeDAO.addAnimeStatus(
            AnimeStatusPersistence(
                personalStatus
            )
        )
        val animeToInsert = AnimePersonalStatusPersistence(
            1,
            5,
            personalStatus,
            1,
            System.currentTimeMillis()
        )
        personalAnimeLocalDataSource.savePersonalAnimeStatus(animeToInsert)
        val asyncJob = syncJobDAO.getPersonalAnimeListSyncJob(animeId = 1)
        assertThat(asyncJob).isNotNull()
        assertThat(asyncJob.deleted).isFalse()
    }

    @Test
    fun deleteAnimePersonalStatusPersistenceShouldAddDeleteSyncWork() = runTest {
        val personalStatus = AnimeStatusValue.Dropped.presentIndex
        animeDAO.addAnime(
            AnimePersistence(
                id = 1,
                title = "test",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        )
        personalAnimeDAO.addAnimeStatus(
            AnimeStatusPersistence(
                personalStatus
            )
        )
        val animeToInsert = AnimePersonalStatusPersistence(
            1,
            5,
            personalStatus,
            1,
            System.currentTimeMillis()
        )
        personalAnimeLocalDataSource.savePersonalAnimeStatus(animeToInsert)
        personalAnimeLocalDataSource.deleteAnimePersonalStatus(animeToInsert.animeId)
        val asyncJob = syncJobDAO.getPersonalAnimeListSyncJob(animeId = 1)
        assertThat(asyncJob).isNotNull()
        assertThat(asyncJob.deleted).isTrue()
    }
}
