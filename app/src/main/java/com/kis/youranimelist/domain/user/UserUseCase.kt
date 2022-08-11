package com.kis.youranimelist.domain.user

import com.kis.youranimelist.data.repository.user.UserRepository
import com.kis.youranimelist.domain.Result
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.lang.IllegalArgumentException
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
) {
    fun getUserData(): Flow<Result<User>> {
        return userRepository.getUser().map<User, Result<User>> { userNoBackground ->
            if (userNoBackground.favouriteAnime == null) {
                val favAnime = personalAnimeListUseCase.getRandomFavouriteAnime()
                userRepository.updateFavoriteAnime(favAnime)
                Result.Success(userNoBackground.copy(favouriteAnime = favAnime))
            } else {
                Result.Success(userNoBackground)
            }
        }.catch { e: Throwable ->
            emit(Result.Error(e))
        }
    }
}
