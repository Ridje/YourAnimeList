package com.kis.youranimelist.data.network.model.personallist

import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class AnimeStatusResponse(
    @SerialName("status") val status: String,
    @SerialName("score") val score: Int,
    @SerialName("num_episodes_watched") val numEpisodesWatched: Int,
    @SerialName("updated_at") val updatedAt: String,
)

fun AnimeStatusResponse.asAnimePersonalStatusPersistence(
    animeId: Int,
    updatedAtFormatter: (String) -> Long,
): AnimePersonalStatusPersistence {
    return AnimePersonalStatusPersistence(
        score = this.score,
        episodesWatched = this.numEpisodesWatched,
        statusId = this.status,
        animeId = animeId,
        updatedAt = updatedAtFormatter.invoke(this.updatedAt)
    )
}
