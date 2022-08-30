package com.kis.youranimelist.data

interface SyncRepository {
    suspend fun synchronize(): Boolean
}
