package com.kis.youranimelist.domain.personalanimelist

import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

private const val FAVOURITE_ANIME_THRESHOLD = 7

class PersonalAnimeListUseCase @Inject constructor(
    private val personalAnimeRepository: PersonalAnimeRepository,
) {
    fun getPersonalAnimeStatusesProducer(): Flow<ResultWrapper<List<AnimeStatus>>> {
        return personalAnimeRepository.getPersonalAnimeStatusesProducer()
            .transform<List<AnimeStatus>, ResultWrapper<List<AnimeStatus>>> {
                emit(ResultWrapper.Success(it))
            }
            .catch { e ->
                emit(ResultWrapper.Error(e))
            }
    }

    suspend fun refreshPersonalAnimeStatuses(): Boolean {
        return personalAnimeRepository.synchronize()
    }

    suspend fun deletePersonalAnimeStatus(animeId: Int): Boolean {
        return personalAnimeRepository.deletePersonalAnimeStatus(animeId)
    }

    fun getPersonalAnimeStatusProducer(id: Int): Flow<ResultWrapper<AnimeStatus>> {
        return personalAnimeRepository.getPersonalAnimeStatusProducer(id)
            .transform {
                val resultWrapper = it?.let {
                    ResultWrapper.Success(it)
                } ?: ResultWrapper.Loading
                emit(resultWrapper)
            }.catch { e ->
                emit(ResultWrapper.Error(e))
            }
    }

    suspend fun savePersonalAnimeStatus(newStatus: AnimeStatus): Boolean {
        return personalAnimeRepository.saveAnimeStatus(newStatus)
    }

    suspend fun deletePersonalSyncData() {
        personalAnimeRepository.deleteSyncPersonalData()
    }

    fun getRandomFavouriteAnimeProducer(): Flow<Anime?> {
        return personalAnimeRepository.getPersonalAnimeStatusesProducer()
            .take(1)
            .map {
                it.filter { anime -> anime.score > FAVOURITE_ANIME_THRESHOLD }.randomOrNull()?.anime
            }
    }
}
