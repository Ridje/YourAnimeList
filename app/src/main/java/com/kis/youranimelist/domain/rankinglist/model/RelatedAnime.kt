package com.kis.youranimelist.domain.rankinglist.model

import android.os.Parcelable
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.parcelize.Parcelize

@Parcelize
data class RelatedAnime(
    val anime: Anime,
    val relatedTypeFormatted: String,
    val relatedType: String,
) : Parcelable
