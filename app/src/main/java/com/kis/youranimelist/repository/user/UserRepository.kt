package com.kis.youranimelist.repository.user

import com.kis.youranimelist.model.app.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userCache: User?

    fun getUser(): Flow<User>
}
