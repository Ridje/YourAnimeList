package com.kis.youranimelist.data.cache.model.syncjob

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence

@Entity("deferred_personal_anime_list_change", foreignKeys = [ForeignKey(
    entity = AnimePersistence::class,
    parentColumns = ["id"],
    childColumns = ["anime_id"])]
)
data class DeferredPersonalAnimeListChange(
    @ColumnInfo("anime_id")
    @PrimaryKey
    val animeId: Int,
    val deleted: Boolean,
    @ColumnInfo("change_timestamp")
    val changeTimestamp: Long,
)
