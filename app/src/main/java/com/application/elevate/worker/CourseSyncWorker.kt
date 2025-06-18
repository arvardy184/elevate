package com.application.elevate.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.application.elevate.data.repository.CourseRepository
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.util.NetworkMonitor
import com.application.elevate.util.NotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import android.util.Log

@HiltWorker
class CourseSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
    private val notificationManager: NotificationManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "CourseSyncWorker"
        const val WORK_NAME = "course_sync_work"
        
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<CourseSyncWorker>()
                .setConstraints(constraints)
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting course sync...")
            
            // Check if we have network connectivity
            if (!networkMonitor.isCurrentlyOnline()) {
                Log.d(TAG, "No network connectivity, skipping sync")
                return Result.retry()
            }

            // Get auth token
            val token = userRepository.getAuthToken()
            if (token == null) {
                Log.e(TAG, "No auth token available")
                return Result.failure()
            }

            // Sync pending course data
            val syncResult = courseRepository.syncPendingData(token)
            
            if (syncResult.isSuccess) {
                Log.d(TAG, "Course sync completed successfully")
                
                // Show success notification
                notificationManager.showQuizSyncSuccess(
                    "Jawaban quiz berhasil dikirim!"
                )
                
                Result.success()
            } else {
                Log.e(TAG, "Course sync failed: ${syncResult.exceptionOrNull()?.message}")
                
                // Show error notification
                notificationManager.showSyncError(
                    "Gagal mengirim jawaban quiz. Akan dicoba lagi nanti."
                )
                
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Course sync error: ${e.message}")
            Result.failure()
        }
    }
}

// Entry point for Dagger Hilt
@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface CourseSyncWorkerEntryPoint {
    fun courseRepository(): CourseRepository
    fun userRepository(): UserRepository
    fun networkMonitor(): NetworkMonitor
    fun notificationManager(): NotificationManager
} 