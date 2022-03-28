package com.kis.youranimelist.model.api

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartSeason(@JsonProperty("year") var year: Int, @JsonProperty("season") var season: String) : Parcelable
