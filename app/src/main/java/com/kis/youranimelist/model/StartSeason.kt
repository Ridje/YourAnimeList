package com.kis.youranimelist.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StartSeason(@JsonProperty("year") var year: Int, @JsonProperty("season") var season: String) : Parcelable
