package com.application.elevate.util

import android.content.Context
import android.util.Log
import androidx.work.*
import com.application.elevate.worker.ProfileSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val context: Context,
    private val networkMonitor: NetworkMonitor
) {
    companion object {
        private const val TAG = "SyncManager"
        private const val SYNC_WORK_TAG = "SYNC_WORK"
    }

    private val workManager = WorkManager.getInstance(context)
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Monitor network changes and trigger sync when online
        networkMonitor.isOnline()
            .onEach { isOnline ->
                if (isOnline) {
                    Log.d(TAG, "Network is online, triggering sync")
                    triggerImmediateSync()
                } else {
                    Log.d(TAG, "Network is offline")
                }
            }
            .launchIn(coroutineScope)
    }

    /**
     * Schedule periodic sync work
     */
    fun schedulePeriodicSync() {
        Log.d(TAG, "Scheduling periodic sync")
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<ProfileSyncWorker>(
            repeatInterval = 2, // 2 hours
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(SYNC_WORK_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            ProfileSyncWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    /**
     * Trigger immediate sync
     */
    fun triggerImmediateSync() {
        Log.d(TAG, "Triggering immediate sync")
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val immediateWorkRequest = OneTimeWorkRequestBuilder<ProfileSyncWorker>()
            .setConstraints(constraints)
            .addTag(SYNC_WORK_TAG)
            .build()

        workManager.enqueueUniqueWork(
            "immediate_sync",
            ExistingWorkPolicy.REPLACE,
            immediateWorkRequest
        )
    }

    /**
     * Cancel all sync work
     */
    fun cancelAllSync() {
        Log.d(TAG, "Cancelling all sync work")
        workManager.cancelAllWorkByTag(SYNC_WORK_TAG)
    }

    /**
     * Get sync work status
     */
    fun getSyncWorkStatus() = workManager.getWorkInfosByTagLiveData(SYNC_WORK_TAG)
} 