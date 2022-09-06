package com.kis.youranimelist.di

import android.content.Context
import androidx.room.Room
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SideDAO
import com.kis.youranimelist.data.cache.dao.SyncJobDao
import com.kis.youranimelist.data.cache.dao.UserDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RoomModule::class])
class TestRoomModule {

    @Singleton
    @Provides
    fun provideUserDatabase(
        @ApplicationContext context: Context,
    ): UserDatabase {
        return Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).build()
    }

    @Singleton
    @Provides
    fun provideAnimeDAO(
        database: UserDatabase
    ): AnimeDAO {
        return database.animeDAO()
    }

    @Singleton
    @Provides
    fun providePersonalAnimeDAO(
        database: UserDatabase
    ): PersonalAnimeDAO {
        return database.personalAnimeStatusesDAO()
    }

    @Singleton
    @Provides
    fun provideUserDAO(
        database: UserDatabase,
    ): UserDAO {
        return database.userDAO()
    }

    @Singleton
    @Provides
    fun provideSideDAO(
        database: UserDatabase
    ): SideDAO {
        return database.sideDAO()
    }

    @Singleton
    @Provides
    fun provideSyncJobDao(
        database: UserDatabase
    ): SyncJobDao {
        return database.syncJobDAO()
    }
}
