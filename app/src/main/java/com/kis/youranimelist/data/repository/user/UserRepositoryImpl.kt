package com.kis.youranimelist.data.repository.user

import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.user.model.User
import com.kis.youranimelist.domain.user.mapper.UserMapper
import com.kis.youranimelist.data.repository.RemoteDataSource
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
