package com.kis.youranimelist.domain.rankinglist.model

import android.os.Parcelable
import com.kis.youranimelist.data.network.model.PictureResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class Picture(
    var large: String?,
    var medium: String?,
) : Parcelable {
    constructor(response: PictureResponse?) : this(response?.large, response?.medium)
}
