package com.kis.youranimelist.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.kis.youranimelist.R
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

private const val SYNC_NOTIFICATION_ID = 0
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val personalAnimeRepository: PersonalAnimeRepository,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(applicationContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        applicationContext.syncForegroundInfo()

    override suspend fun doWork(): Result {
        val result = withContext(ioDispatcher) {
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

fun Context.syncForegroundInfo() = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification()
)

private fun Context.syncWorkNotification(): Notification {
    val channel = NotificationChannel(
        SYNC_NOTIFICATION_CHANNEL_ID,
        getString(R.string.sync_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = getString(R.string.sync_notification_channel_description)
    }
    // Register the channel with the system
    val notificationManager: NotificationManager? =
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    notificationManager?.createNotificationChannel(channel)

    return NotificationCompat.Builder(this, SYNC_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_myanimelist)
        .setContentTitle(getString(R.string.sync_notification_title))
        .setProgress(0, 0, true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
