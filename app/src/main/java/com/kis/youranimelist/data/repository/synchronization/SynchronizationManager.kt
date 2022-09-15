package com.kis.youranimelist.data.repository.synchronization

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import com.kis.youranimelist.core.utils.Setting
import com.kis.youranimelist.data.SyncWorker
import javax.inject.Inject

interface SynchronizationManager {
    fun planSynchronization()
    fun cancelPlannedSynchronizations()
}

class SynchronizationManagerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val appPreferences: AppPreferencesWrapper,
) : SynchronizationManager {
    override fun planSynchronization() {
        if (!appPreferences.readValue(Setting.UseAppAuth)) {
            workManager.enqueueUniqueWork(
                SyncWorker.SyncWorkName,
                ExistingWorkPolicy.REPLACE,
                SyncWorker.startSyncJob()
            )
        }
    }

    override fun cancelPlannedSynchronizations() {
        workManager.cancelUniqueWork(SyncWorker.SyncWorkName)
    }
}
