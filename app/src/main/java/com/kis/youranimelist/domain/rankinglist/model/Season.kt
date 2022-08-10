package com.kis.youranimelist.domain.rankinglist.model

import android.os.Parcelable
import com.kis.youranimelist.data.network.model.StartSeasonResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class Season(
    val year: Int?,
    val season: String?,
) : Parcelable {
    constructor(season: StartSeasonResponse?) : this(season?.year, season?.season)
}
