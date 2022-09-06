package com.kis.youranimelist.data.cache.localdatasource

import com.kis.youranimelist.core.utils.returnCatchingWithCancellation
import com.kis.youranimelist.core.utils.runCatchingWithCancellation
import com.kis.youranimelist.data.cache.dao.UserDAO
import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UserLocalDataSource {
    suspend fun updateUserData(user: UserPersistence)
    suspend fun getUserData(): UserPersistence?
    suspend fun clearUserData(): Boolean
}

class UserLocalDataSourceImpl @Inject constructor(
    private val userDAO: UserDAO,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : UserLocalDataSource {
    override suspend fun updateUserData(user: UserPersistence) {
        withContext(ioDispatcher) {
            runCatchingWithCancellation { userDAO.addUserDataKeepSingle(user) }
        }
    }

    override suspend fun getUserData(): UserPersistence? =
        withContext(ioDispatcher) { runCatchingWithCancellation { userDAO.getUserData() } }

    override suspend fun clearUserData(): Boolean = withContext(ioDispatcher) {
        returnCatchingWithCancellation {
            userDAO.clearUserData()
        }
    }
}
