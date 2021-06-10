package com.kis.youranimelist.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val DB_NAME = "localbase.dt"

@Module
@InstallIn(SingletonComponent::class)
class RoomModule() {

    @Singleton
    @Provides
    fun provideUserDatabase(@ApplicationContext context: Context) : UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            DB_NAME)
            .build()
    }
}