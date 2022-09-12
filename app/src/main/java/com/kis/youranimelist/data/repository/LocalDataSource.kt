package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.cache.localdatasource.AnimeLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.PersonalAnimeLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SyncJobLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource

interface LocalDataSource :
    UserLocalDataSource,
    SideLocalDataSource,
    SyncJobLocalDataSource,
    AnimeLocalDataSource,
    PersonalAnimeLocalDataSource {

    suspend fun deleteSyncData(): Boolean
}
