package com.application.elevate.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.application.elevate.data.repository.ProfileRepository
import com.application.elevate.util.NetworkUtil
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collect

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProfileSyncWorkerEntryPoint {
    fun profileRepository(): ProfileRepository
    fun networkUtil(): NetworkUtil
}

class ProfileSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "ProfileSyncWorker"
        const val UNIQUE_WORK_NAME = "profile_sync_work"
    }

    private val entryPoint = EntryPointAccessors.fromApplication(
        context,
        ProfileSyncWorkerEntryPoint::class.java
    )
    
    private val profileRepository = entryPoint.profileRepository()
    private val networkUtil = entryPoint.networkUtil()

    override suspend fun doWork(): ListenableWorker.Result {
        Log.d(TAG, "Starting ProfileSyncWorker")
        
        return try {
            // Check if device is online
            if (!networkUtil.isOnline()) {
                Log.d(TAG, "Device offline, skipping sync")
                return ListenableWorker.Result.retry()
            }

            // Perform sync
            Log.d(TAG, "Device online, starting sync...")
            var syncResult: kotlin.Result<Boolean>? = null
            
            profileRepository.syncProfile().collect { result ->
                syncResult = result
                result.onSuccess { synced ->
                    Log.d(TAG, "Profile sync completed successfully: $synced")
                }.onFailure { error ->
                    Log.e(TAG, "Profile sync failed: ${error.message}")
                }
            }

            // Determine worker result based on sync result
            when {
                syncResult?.isSuccess == true -> {
                    Log.d(TAG, "ProfileSyncWorker completed successfully")
                    ListenableWorker.Result.success()
                }
                syncResult?.isFailure == true -> {
                    Log.e(TAG, "ProfileSyncWorker failed: ${syncResult?.exceptionOrNull()?.message}")
                    ListenableWorker.Result.retry()
                }
                else -> {
                    Log.e(TAG, "ProfileSyncWorker completed with unknown result")
                    ListenableWorker.Result.failure()
                }
            }
            
        } catch (exception: Exception) {
            Log.e(TAG, "ProfileSyncWorker failed with exception: ${exception.message}")
            ListenableWorker.Result.failure()
        }
    }
} 