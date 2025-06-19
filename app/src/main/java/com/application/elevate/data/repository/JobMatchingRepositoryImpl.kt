package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.JobMatchingApiService
import com.application.elevate.util.NetworkMonitor
import com.application.elevate.model.*
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobMatchingRepositoryImpl @Inject constructor(
    private val apiService: JobMatchingApiService,
    private val userRepository: UserRepository,
    private val offlineRepository: JobMatchingOfflineRepository,
    private val networkMonitor: NetworkMonitor
) : JobMatchingRepository {

    companion object {
        private const val TAG = "JobMatchingRepository"
    }

    override suspend fun uploadAndMatchJobs(
        cvFile: File,
        dreamJob: String
    ): JobMatchingResult {
        return try {
            Log.d(TAG, "Starting job matching upload for dream job: $dreamJob")
            
            // Check network connectivity
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (!isOnline) {
                // Save offline and return special result indicating offline mode
                Log.d(TAG, "Device is offline, saving data locally")
                val offlineId = offlineRepository.saveOfflineJobMatching(cvFile, dreamJob)
                
                // Create offline response
                val offlineEntity = offlineRepository.getJobMatchingByIdLocal(offlineId)
                offlineEntity?.let { entity ->
                    val offlineResponse = JobMatchingResponse(
                        status = "offline",
                        message = "Data disimpan offline, akan disinkronkan ketika online",
                        data = JobMatchingData(
                            id = entity.id,
                            dreamJob = entity.dreamJob,
                            matches = entity.matches,
                            aiAnalysis = entity.aiAnalysis,
                            createdAt = entity.createdAt,
                            totalMatches = entity.totalMatches,
                            cvSaved = entity.cvSaved
                        )
                    )
                    return JobMatchingResult.Success(offlineResponse)
                }
                
                return JobMatchingResult.Error("Gagal menyimpan data offline")
            }
            
            // Online flow - proceed with API call
            val token = userRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                return JobMatchingResult.Error("Token not found. Please login again.")
            }
            
            // Format token properly with Bearer prefix (consistent with other repositories)
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

            // Prepare multipart data
            val dreamJobBody = dreamJob.toRequestBody("text/plain".toMediaTypeOrNull())
            
            // Rename file to cv.pdf as required by API
            val renamedFile = if (cvFile.name != "cv.pdf") {
                val newFile = File(cvFile.parent, "cv.pdf")
                cvFile.copyTo(newFile, overwrite = true)
                newFile
            } else {
                cvFile
            }

            val requestFile = renamedFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            val cvPart = MultipartBody.Part.createFormData("cv", "cv.pdf", requestFile)

            Log.d(TAG, "Sending request to job matching API")
            
            val response = apiService.uploadAndMatchJobs(
                authorization = formattedToken,
                dreamJob = dreamJobBody,
                cv = cvPart
            )

            if (response.isSuccessful) {
                response.body()?.let { jobMatchingResponse ->
                    // Save to local database for offline access
                    offlineRepository.saveJobMatchingLocal(jobMatchingResponse, isOfflineData = false)
                    
                    Log.d(TAG, "Job matching successful: ${jobMatchingResponse.data.totalMatches} matches found")
                    JobMatchingResult.Success(jobMatchingResponse)
                } ?: JobMatchingResult.Error("Empty response from server")
            } else {
                val errorMessage = "Job matching failed: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMessage)
                JobMatchingResult.Error(errorMessage)
            }

        } catch (e: Exception) {
            val errorMessage = "Job matching error: ${e.localizedMessage}"
            Log.e(TAG, errorMessage, e)
            JobMatchingResult.Error(errorMessage)
        }
    }

    override suspend fun getJobMatchingHistory(): JobMatchingHistoryResult {
        return try {
            Log.d(TAG, "Getting job matching history")
            
            val isOnline = networkMonitor.isCurrentlyOnline()
            
            if (!isOnline) {
                // Return local data when offline
                Log.d(TAG, "Device is offline, returning local history")
                return getLocalJobMatchingHistory()
            }
            
            // Online flow - try to get from API first
            val token = userRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                // If no token but we have local data, return local data
                return getLocalJobMatchingHistory()
            }
            
            // Format token properly with Bearer prefix
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

            val response = apiService.getJobMatchingHistory(authorization = formattedToken)

            if (response.isSuccessful) {
                response.body()?.let { historyResponse ->
                    // Save API response to local database
                    for (item in historyResponse.data) {
                        val jobMatchingResponse = JobMatchingResponse(
                            status = "success",
                            message = "Data retrieved successfully",
                            data = JobMatchingData(
                                id = item.id,
                                dreamJob = item.dreamJob,
                                matches = item.matches,
                                aiAnalysis = item.aiAnalysis,
                                createdAt = item.createdAt,
                                totalMatches = item.matches.size,
                                cvSaved = !item.cvreview.isNullOrEmpty()
                            )
                        )
                        offlineRepository.saveJobMatchingLocal(jobMatchingResponse, isOfflineData = false)
                    }
                    
                    Log.d(TAG, "Job matching history retrieved: ${historyResponse.total} items")
                    JobMatchingHistoryResult.Success(historyResponse)
                } ?: getLocalJobMatchingHistory()
            } else {
                val errorMessage = "Failed to get history: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMessage)
                
                // Try to return local data as fallback
                val localResult = getLocalJobMatchingHistory()
                if (localResult is JobMatchingHistoryResult.Success) {
                    return localResult
                }
                
                JobMatchingHistoryResult.Error(errorMessage)
            }

        } catch (e: Exception) {
            val errorMessage = "Error getting history: ${e.localizedMessage}"
            Log.e(TAG, errorMessage, e)
            
            // Try to return local data as fallback
            val localResult = getLocalJobMatchingHistory()
            if (localResult is JobMatchingHistoryResult.Success) {
                return localResult
            }
            
            JobMatchingHistoryResult.Error(errorMessage)
        }
    }
    
    private suspend fun getLocalJobMatchingHistory(): JobMatchingHistoryResult {
        return try {
            // Get local data using .first() to get single emission from Flow
            val localEntities = offlineRepository.getAllJobMatchingLocal().first()
            
            if (localEntities.isEmpty()) {
                JobMatchingHistoryResult.Error("Tidak ada data tersimpan secara lokal")
            } else {
                // Convert local entities to API format
                val historyItems = localEntities.map { entity ->
                    JobMatchingHistoryItem(
                        id = entity.id,
                        userId = 0, // We don't store userId locally
                        cvReviewId = null,
                        dreamJob = entity.dreamJob,
                        matches = entity.matches,
                        aiAnalysis = entity.aiAnalysis,
                        createdAt = entity.createdAt,
                        updatedAt = entity.createdAt,
                        cvreview = entity.cvFilePath
                    )
                }
                
                val historyResponse = JobMatchingHistoryResponse(
                    status = "success",
                    message = "Data lokal berhasil dimuat",
                    data = historyItems,
                    total = historyItems.size
                )
                
                Log.d(TAG, "Local job matching history retrieved: ${historyItems.size} items")
                JobMatchingHistoryResult.Success(historyResponse)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local history", e)
            JobMatchingHistoryResult.Error("Gagal mengambil data lokal: ${e.localizedMessage}")
        }
    }
    
    override suspend fun syncPendingData(): List<SyncResult> {
        return try {
            Log.d(TAG, "Starting sync pending data")
            
            if (!networkMonitor.isCurrentlyOnline()) {
                Log.d(TAG, "Device is offline, cannot sync")
                return emptyList()
            }
            
            offlineRepository.syncPendingData()
        } catch (e: Exception) {
            Log.e(TAG, "Error during sync", e)
            emptyList()
        }
    }
    
    override suspend fun getUnsyncedCount(): Int {
        return try {
            offlineRepository.getUnsyncedJobMatching().size
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unsynced count", e)
            0
        }
    }
} 