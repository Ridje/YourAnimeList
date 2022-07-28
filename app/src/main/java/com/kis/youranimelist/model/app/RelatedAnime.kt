package com.kis.youranimelist.model.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RelatedAnime(
    val anime: Anime,
    val relatedTypeFormatted: String,
    val relatedType: String,
) : Parcelable
