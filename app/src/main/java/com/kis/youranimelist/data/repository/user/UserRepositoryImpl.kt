package com.kis.youranimelist.data.repository.user

import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.model.asResult
import com.kis.youranimelist.domain.user.mapper.UserMapper
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val userMapper: UserMapper,
) : UserRepository {

    override fun getUser(): Flow<ResultWrapper<User>> {
        return flow {
            val cacheData = localDataSource.getUserCache()
            if (cacheData != null) { emit(ResultWrapper.Success(userMapper.map(cacheData))) }

            val remoteConvertedResult = remoteDataSource
                .getUserData()
                .asResult { response -> userMapper.map(response) }
            if (remoteConvertedResult is ResultWrapper.Success) {
                localDataSource.updateUserCache(remoteConvertedResult.data)
            }
            if (remoteConvertedResult is ResultWrapper.Error && cacheData != null) {
                return@flow
            }
            emit(remoteConvertedResult)
        }
    }

    override suspend fun clearUserProfile(): Boolean {
        return localDataSource.clearUserData()
    }
}
