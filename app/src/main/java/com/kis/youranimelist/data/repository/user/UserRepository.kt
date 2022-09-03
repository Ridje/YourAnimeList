package com.kis.youranimelist.data.repository.user

import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<ResultWrapper<User>>
    suspend fun clearUserProfile(): Boolean
}
