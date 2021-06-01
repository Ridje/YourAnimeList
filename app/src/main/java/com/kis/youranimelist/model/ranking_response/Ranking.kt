package com.kis.youranimelist.model.ranking_response

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ranking(@JsonProperty("rank") var rank : Int, @JsonProperty("previous_rank") var previousRank : Int?) : Parcelable
