package com.kis.youranimelist.data.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserPersistence(
    @PrimaryKey
    val id: Int,
    val name: String,
    val gender: String?,
    val picture: String?,
    val birthday: String?,
    val location: String?,
    @ColumnInfo(name = "joined_at")
    val joinedAt: String?,
    @ColumnInfo(name = "items_watching")
    val itemsWatching: Int?,
    @ColumnInfo(name = "items_completed")
    val itemsCompleted: Int?,
    @ColumnInfo(name = "items_on_hold")
    val itemsOnHold: Int?,
    @ColumnInfo(name = "items_dropped")
    val itemsDropped: Int?,
    @ColumnInfo(name = "items_plan_to_watch")
    val itemsPlanToWatch: Int?,
    val items: Int?,
    @ColumnInfo(name = "days_watched")
    val daysWatched: Float?,
    @ColumnInfo(name = "days_watching")
    val daysWatching: Float?,
    @ColumnInfo(name = "days_completed")
    val daysCompleted: Float?,
    @ColumnInfo(name = "days_on_hold")
    val daysOnHold: Float?,
    @ColumnInfo(name = "days_dropped")
    val daysDropped: Float?,
    val days: Float?,
    val episodes: Int?,
    val meanScore: Float?,
)
