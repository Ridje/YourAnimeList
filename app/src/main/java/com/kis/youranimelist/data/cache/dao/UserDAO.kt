package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kis.youranimelist.data.cache.model.UserPersistence
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUserData(user: UserPersistence)

    @Query("DELETE FROM user")
    fun clearUserData()

    @Query("SELECT * FROM user LIMIT 1")
    fun getUserData(): UserPersistence?
}
