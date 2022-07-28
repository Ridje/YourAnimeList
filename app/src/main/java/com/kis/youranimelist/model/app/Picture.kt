package com.kis.youranimelist.model.app

import android.os.Parcelable
import com.kis.youranimelist.model.api.PictureResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class Picture(
    var large: String?,
    var medium: String?,
) : Parcelable {
    constructor(response: PictureResponse?) : this(response?.large, response?.medium)
}
