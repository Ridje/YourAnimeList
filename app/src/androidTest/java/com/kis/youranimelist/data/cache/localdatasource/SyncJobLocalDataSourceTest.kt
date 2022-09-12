package com.kis.youranimelist.data.cache.localdatasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.SyncJobDao
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
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
class SyncJobLocalDataSourceTest {

    @get:Rule(order = 0)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var syncJobLocalDataSource: SyncJobLocalDataSource

    @Inject
    lateinit var syncJobDAO: SyncJobDao

    @Inject
    lateinit var animeDAO: AnimeDAO

    private val anime = AnimePersistence(
        1,
        "Some anime",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )

    private val secondAnime = AnimePersistence(
        2,
        "Some anime 2",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )

    private val changeJob = DeferredPersonalAnimeListChange(
        anime.id,
        false,
        System.currentTimeMillis(),
    )
    private val deleteJob = DeferredPersonalAnimeListChange(
        anime.id,
        true,
        System.currentTimeMillis(),
    )
    private val anotherDeleteJob = DeferredPersonalAnimeListChange(
        secondAnime.id,
        true,
        System.currentTimeMillis(),
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testAddSyncJob() = runTest {
        animeDAO.addAnime(anime)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(changeJob)
        assertThat(syncJobDAO.getPersonalAnimeListSyncJob(anime.id)).isEqualTo(changeJob)
    }

    @Test
    fun testMultiplyAddSyncJobOnSameAnimeReplacesEachOther() = runTest {
        animeDAO.addAnime(anime)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(changeJob)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(deleteJob)
        assertThat(syncJobDAO.getPersonalAnimeListSyncJob(anime.id)).isEqualTo(deleteJob)
        assertThat(syncJobDAO.getPersonalAnimeListSyncJob(anime.id)).isNotEqualTo(changeJob)
    }

    @Test
    fun deleteAllSyncJobs() = runTest {
        animeDAO.addAnime(anime)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(changeJob)
        assertThat(syncJobDAO.getPersonalAnimeListSyncJobs()).isNotEmpty()
        syncJobLocalDataSource.deleteAllSyncJobs()
        assertThat(syncJobDAO.getPersonalAnimeListSyncJobs()).isEmpty()
    }

    @Test
    fun deleteAllSyncJobsByList() = runTest {
        animeDAO.addAnime(anime)
        animeDAO.addAnime(secondAnime)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(changeJob)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(anotherDeleteJob)
        assertThat(syncJobLocalDataSource.getPersonalAnimeListSyncJobs()).hasSize(2)
        syncJobLocalDataSource.removePersonalAnimeListSyncJob(
            listOf(changeJob, anotherDeleteJob)
        )
        assertThat(syncJobLocalDataSource.getPersonalAnimeListSyncJobs()).hasSize(0)
    }

    @Test
    fun deleteAllSyncJobsByAnimeId() = runTest {
        animeDAO.addAnime(anime)
        animeDAO.addAnime(secondAnime)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(changeJob)
        syncJobLocalDataSource.addPersonalAnimeListSyncJob(anotherDeleteJob)
        syncJobLocalDataSource.removePersonalAnimeListSyncJob(changeJob.animeId)
        assertThat(syncJobLocalDataSource.getPersonalAnimeListSyncJobs()).isEqualTo(listOf(
            anotherDeleteJob))
    }
}
