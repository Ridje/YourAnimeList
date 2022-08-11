package com.kis.youranimelist.data.repository.user

import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
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

    override fun getUser(): Flow<User> {
        return flow {
            val cacheData = localDataSource.getUserCache()
            if (cacheData != null) {
                emit(userMapper.map(cacheData))
            }
            remoteDataSource.getUserData()?.let { userResponse ->
                val mappedResult = userMapper.map(userResponse)
                localDataSource.updateUserCache(mappedResult)
                emit(mappedResult)
            }
        }
    }

}
