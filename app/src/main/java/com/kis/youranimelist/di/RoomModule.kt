package com.kis.youranimelist.di

import android.content.Context
import androidx.room.Room
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SideDAO
import com.kis.youranimelist.data.cache.dao.UserDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val DB_NAME = "localbase.dt"

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun provideUserDatabase(
        @ApplicationContext context: Context,
    ): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
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
}
