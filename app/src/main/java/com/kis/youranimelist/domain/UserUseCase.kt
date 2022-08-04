package com.kis.youranimelist.domain

import com.kis.youranimelist.model.app.User
import com.kis.youranimelist.repository.AnimeRepository
import com.kis.youranimelist.repository.RemoteDataSource
import com.kis.youranimelist.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val animeRepository: AnimeRepository,
) {
    fun getUserData(): Flow<User> {
        return userRepository.getUser().map { userNoBackground ->
            return@map userNoBackground.copy(favouriteAnime = animeRepository.getFavouriteAnime())
        }
    }
}
