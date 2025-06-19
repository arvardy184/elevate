package com.application.elevate.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.application.elevate.data.repository.AssessmentOfflineRepository
import com.application.elevate.util.NetworkUtil
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collect

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AssessmentSyncWorkerEntryPoint {
    fun assessmentOfflineRepository(): AssessmentOfflineRepository
    fun networkUtil(): NetworkUtil
}

class AssessmentSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "AssessmentSyncWorker"
        const val UNIQUE_WORK_NAME = "assessment_sync_work"
    }

    private val entryPoint = EntryPointAccessors.fromApplication(
        context,
        AssessmentSyncWorkerEntryPoint::class.java
    )
    
    private val assessmentRepository = entryPoint.assessmentOfflineRepository()
    private val networkUtil = entryPoint.networkUtil()

    override suspend fun doWork(): ListenableWorker.Result {
        Log.d(TAG, "Starting AssessmentSyncWorker")
        
        return try {
            // Check if device is online
            if (!networkUtil.isOnline()) {
                Log.d(TAG, "Device offline, skipping sync")
                return ListenableWorker.Result.retry()
            }

            // Perform sync
            Log.d(TAG, "Device online, starting sync...")
            var syncResult: kotlin.Result<Boolean>? = null
            
            assessmentRepository.syncAssessments().collect { result ->
                syncResult = result
                result.onSuccess { synced ->
                    Log.d(TAG, "Assessment sync completed successfully: $synced")
                }.onFailure { error ->
                    Log.e(TAG, "Assessment sync failed: ${error.message}")
                }
            }

            // Determine worker result based on sync result
            when {
                syncResult?.isSuccess == true -> {
                    Log.d(TAG, "AssessmentSyncWorker completed successfully")
                    ListenableWorker.Result.success()
                }
                syncResult?.isFailure == true -> {
                    Log.e(TAG, "AssessmentSyncWorker failed: ${syncResult?.exceptionOrNull()?.message}")
                    ListenableWorker.Result.retry()
                }
                else -> {
                    Log.e(TAG, "AssessmentSyncWorker completed with unknown result")
                    ListenableWorker.Result.failure()
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "AssessmentSyncWorker exception: ${e.message}")
            ListenableWorker.Result.failure()
        }
    }
} 