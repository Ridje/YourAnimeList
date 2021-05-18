package com.kis.youranimelist.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Anime(val id:Int, val title:String, val mainPictureLarge:String?, val year:Int?, val mean:Float?, val synopsys:String? = "Description") : Parcelable
