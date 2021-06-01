package com.kis.youranimelist.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import kotlinx.android.parcel.Parcelize

@JsonPropertyOrder("medium","large")
@Parcelize
data class MainPicture(@JsonProperty("large") var large : String?, @JsonProperty("medium") var medium : String) : Parcelable
