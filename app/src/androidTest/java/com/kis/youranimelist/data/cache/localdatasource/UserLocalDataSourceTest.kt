package com.kis.youranimelist.data.cache.localdatasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kis.youranimelist.data.cache.model.UserPersistence
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class UserLocalDataSourceTest {

    @get:Rule(order = 0)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    private val userBoboka = UserPersistence(
        1,
        "Boboka",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )

    private val userJhonny = userBoboka.copy(id = 2)

    @Inject
    lateinit var userLocalDataSource: UserLocalDataSource

    @Before
    fun init() {
        hiltRule.inject()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun dbShouldReturnNullIfNoUserData() = runTest {
        val userDataNull = userLocalDataSource.getUserData()
        assertThat(userDataNull).isNull()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun dbShouldReturnUserJhonnyIfUpdatedJhonnyUserData() = runTest {
        userLocalDataSource.updateUserData(userBoboka)
        val userDataOnlyOneBoboka = userLocalDataSource.getUserData()
        assertThat(userDataOnlyOneBoboka).isEqualTo(userBoboka)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun dbShouldReturnLastUserIfUpdatedTwoDifferentUserData() = runTest {
        assertThat(userLocalDataSource.getUserData()).isNull()
        userLocalDataSource.updateUserData(userBoboka)
        userLocalDataSource.updateUserData(userJhonny)
        assertThat(userLocalDataSource.getUserData()).isEqualTo(userJhonny)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun userDataShouldBeEmptyAfterItCleared() = runTest {
        userLocalDataSource.updateUserData(userBoboka)
        val insertedUser = userLocalDataSource.getUserData()
        assertThat(insertedUser).isEqualTo(userBoboka)
        userLocalDataSource.clearUserData()
        val emptyUserData = userLocalDataSource.getUserData()
        assertThat(emptyUserData).isNull()
    }
}
