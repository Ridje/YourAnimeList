package com.kis.youranimelist.domain.user

import com.kis.youranimelist.data.repository.user.UserRepository
import com.kis.youranimelist.domain.model.Result
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
) {
    fun getUserData(): Flow<Result<User>> {
        return combine(
            userRepository.getUser()
                .map<User, Result<User>> { userNoBackground -> Result.Success(userNoBackground) }
                .catch { e: Throwable ->
                    if (e is CancellationException) {
                        throw e
                    } else {
                        emit(Result.Error(e))
                    }
                },
            personalAnimeListUseCase.getRandomFavouriteAnimeProducer()
                .catch { e: Throwable ->
                    if (e is CancellationException) {
                        throw e
                    } else {
                        emit(null)
                    }
                }
        ) { result: Result<User>, anime: Anime? ->
            return@combine when (result) {
                is Result.Success -> Result.Success(data = result.data.copy(favouriteAnime = anime))
                else -> result
            }
        }
    }
}

