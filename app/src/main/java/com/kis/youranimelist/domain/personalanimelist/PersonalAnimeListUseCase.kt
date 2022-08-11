package com.kis.youranimelist.domain.personalanimelist

import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.domain.Result
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class PersonalAnimeListUseCase @Inject constructor(
    private val personalAnimeRepository: PersonalAnimeRepository,
) {
    fun getPersonalAnimeListProducer(): Flow<Result<List<AnimeStatus>>> {
        return personalAnimeRepository.getAllDataProducer()
            .transform<List<AnimeStatus>, Result<List<AnimeStatus>>> {
                emit(Result.Success(it))
            }
            .catch { e ->
                emit(Result.Error(e))
            }
    }

    suspend fun getRandomFavouriteAnime(): Anime? {
        return personalAnimeRepository.getAllDataProducer()
            .first()
            .filter { it.score > 7 }
            .randomOrNull()?.anime
    }
}
