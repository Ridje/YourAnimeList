package com.kis.youranimelist.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val personalAnimeRepository: PersonalAnimeRepository,
    private val dispatchers: Dispatchers,
) : CoroutineWorker(applicationContext, workerParams) {
    override suspend fun doWork(): Result {
        val result = withContext(dispatchers.IO) {
            personalAnimeRepository.synchronize()
        }
        return if (result) Result.success() else Result.retry()
    }

    companion object {
        private val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        const val SyncWorkName = "SyncWorkName"

        fun startSyncJob() =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(constraints)
                .build()
    }
}
