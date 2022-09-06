package com.kis.youranimelist.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kis.youranimelist.data.cache.model.UserPersistence

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUserData(user: UserPersistence)

    @Transaction
    fun addUserDataKeepSingle(user: UserPersistence) {
        keepOneUserInDb(user.id)
        addUserData(user)
    }

    @Query("DELETE FROM user WHERE user.id != :userId")
    fun keepOneUserInDb(userId: Int)

    @Query("DELETE FROM user")
    fun clearUserData()

    @Query("SELECT * FROM user")
    fun getUserData(): UserPersistence?
}
