package com.kis.youranimelist.domain.user

import android.webkit.CookieManager
import com.kis.youranimelist.data.repository.user.UserRepository
import com.kis.youranimelist.domain.auth.AuthUseCase
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val loginUseCase: AuthUseCase,
    private val userRepository: UserRepository,
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
) {
    fun getUserData(): Flow<ResultWrapper<User>> {
        return combine(
            userRepository.getUser(),
            personalAnimeListUseCase.getRandomFavouriteAnimeProducer()
                .catch { e: Throwable ->
                    if (e is CancellationException) {
                        throw e
                    } else {
                        emit(null)
                    }
                }
        ) { resultWrapper: ResultWrapper<User>, anime: Anime? ->
            return@combine when (resultWrapper) {
                is ResultWrapper.Success -> ResultWrapper.Success(data = resultWrapper.data.copy(favouriteAnime = anime))
                else -> resultWrapper
            }
        }
    }

    fun isAppAuthorization(): Boolean {
        return !loginUseCase.isClientAuth()
    }

    suspend fun logOut() {
        loginUseCase.clearAuthData()
        userRepository.clearUserProfile()
        personalAnimeListUseCase.deletePersonalSyncData()
        CookieManager.getInstance().removeAllCookies(null)
        loginUseCase.onLogoutFinished()
    }
}

