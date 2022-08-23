package com.kis.youranimelist.domain.personalanimelist

import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.domain.model.Result
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class PersonalAnimeListUseCase @Inject constructor(
    private val personalAnimeRepository: PersonalAnimeRepository,
) {
    fun getPersonalAnimeStatusesProducer(): Flow<Result<List<AnimeStatus>>> {
        return personalAnimeRepository.getPersonalAnimeStatusesProducer()
            .transform<List<AnimeStatus>, Result<List<AnimeStatus>>> {
                emit(Result.Success(it))
            }
            .catch { e ->
                emit(Result.Error(e))
            }
    }

    suspend fun refreshPersonalAnimeStatuses() {
        personalAnimeRepository.refreshPersonalAnimeStatuses()
    }

    suspend fun refreshPersonalAnimeStatus(animeId: Int) {
        personalAnimeRepository.refreshPersonalAnimeStatus(animeId)
    }

    suspend fun deletePersonalAnimeStatus(animeId: Int): Boolean {
        return personalAnimeRepository.deletePersonalAnimeStatus(animeId)
    }

    fun getPersonalAnimeStatusProducer(id: Int): Flow<Result<AnimeStatus>> {
        return personalAnimeRepository.getPersonalAnimeStatusProducer(id)
            .transform {
                val result = it?.let {
                    Result.Success(it)
                } ?: Result.Loading
                emit(result)
            }.catch { e ->
                emit(Result.Error(e))
            }
    }

    suspend fun savePersonalAnimeStatus(newStatus: AnimeStatus): Boolean {
        return personalAnimeRepository.saveAnimeStatus(newStatus)
    }

    fun getRandomFavouriteAnimeProducer(): Flow<Anime?> {
        return personalAnimeRepository.getPersonalAnimeStatusesProducer()
            .take(1)
            .map {
                it.filter { anime -> anime.score > 7 }.randomOrNull()?.anime
            }
    }
}