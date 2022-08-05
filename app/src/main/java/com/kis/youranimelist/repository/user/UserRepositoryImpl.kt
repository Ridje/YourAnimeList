package com.kis.youranimelist.repository.user

import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.User
import com.kis.youranimelist.model.mapper.UserMapper
import com.kis.youranimelist.repository.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val userMapper: UserMapper,
) : UserRepository {
    override var userCache: User? = null

    override fun updateFavoriteAnime(anime: Anime?) {
        userCache = if (anime == null) {
            null
        } else {
            userCache?.copy(
                favouriteAnime = anime
            )
        }
    }

    override fun getUser(): Flow<User> {
        return flow {
            userCache?.let { userCached ->
                emit(userCached)
            }
            val result = userMapper.map(remoteDataSource.getUserData())
            userCache = result
            emit(result)
        }
    }

}
