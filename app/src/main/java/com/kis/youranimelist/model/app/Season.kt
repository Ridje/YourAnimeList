package com.kis.youranimelist.model.app

import android.os.Parcelable
import com.kis.youranimelist.model.api.StartSeasonResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class Season(
    val year: Int?,
    val season: String?,
) : Parcelable {
    constructor(season: StartSeasonResponse?) : this(season?.year, season?.season)
}
