package com.kis.youranimelist.domain.synchronization

import com.kis.youranimelist.data.repository.synchronization.SynchronizationManager
import javax.inject.Inject

class SynchronizationUseCase @Inject constructor(
    private val synchronizationManager: SynchronizationManager,
) {
    fun planSynchronization() {
        synchronizationManager.planSynchronization()
    }
}
