package com.kis.youranimelist.ui.item

import com.kis.youranimelist.model.app.Anime

sealed class ItemState {
    data class Success(val item : Anime) : ItemState()
    data class Error(val error : Throwable) : ItemState()
    object Loading : ItemState()
}
