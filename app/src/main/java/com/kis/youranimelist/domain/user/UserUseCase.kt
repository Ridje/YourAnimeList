package com.kis.youranimelist.domain.user

import com.kis.youranimelist.domain.user.model.User
import com.kis.youranimelist.data.repository.AnimeRepository
import com.kis.youranimelist.data.repository.user.UserRepository
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
