package com.kis.youranimelist.data.network.model.personallist

import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeStatusResponse(
    @SerialName("status") val status: String,
    @SerialName("score") val score: Int,
    @SerialName("num_episodes_watched") val numEpisodesWatched: Int,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("tags") val tags: List<String>,
    @SerialName("comments") val comments: String,
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
        updatedAt = updatedAtFormatter(this.updatedAt),
        comments = comments,
    )
}
