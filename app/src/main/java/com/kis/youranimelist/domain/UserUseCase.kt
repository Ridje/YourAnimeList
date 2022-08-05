package com.kis.youranimelist.domain

import com.kis.youranimelist.model.app.User
import com.kis.youranimelist.repository.AnimeRepository
import com.kis.youranimelist.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val animeRepository: AnimeRepository,
) {
    fun getUserData(): Flow<User> {
        return userRepository.getUser().map { userNoBackground ->
            if (userNoBackground.favouriteAnime == null) {
                val favAnime = animeRepository.getFavouriteAnime()
                userRepository.updateFavoriteAnime(favAnime)
                return@map userNoBackground.copy(favouriteAnime = favAnime)
            } else {
                return@map userNoBackground
            }
        }
    }
}
