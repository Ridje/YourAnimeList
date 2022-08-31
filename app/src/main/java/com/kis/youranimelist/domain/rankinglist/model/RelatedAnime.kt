package com.kis.youranimelist.domain.rankinglist.model

data class RelatedAnime(
    val anime: Anime,
    val relatedTypeFormatted: String,
    val relatedType: String,
)
