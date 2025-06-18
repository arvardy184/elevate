package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.database.dao.JobMatchingDao
import com.application.elevate.data.database.entity.JobMatchingEntity
import com.application.elevate.data.database.entity.JobMatchEntity
import com.application.elevate.data.database.entity.AIAnalysisEntity
import com.application.elevate.data.api.JobMatchingApiService
import com.application.elevate.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobMatchingOfflineRepositoryImpl @Inject constructor(
    private val jobMatchingDao: JobMatchingDao,
    private val apiService: JobMatchingApiService,
    private val userRepository: UserRepository
) : JobMatchingOfflineRepository {

    companion object {
        private const val TAG = "JobMatchingOfflineRepo"
    }

    override fun getAllJobMatchingLocal(): Flow<List<JobMatchingEntity>> {
        return jobMatchingDao.getAllJobMatching()
    }

    override suspend fun getJobMatchingByIdLocal(id: String): JobMatchingEntity? {
        return jobMatchingDao.getJobMatchingById(id)
    }

    override suspend fun saveJobMatchingLocal(
        jobMatchingResponse: JobMatchingResponse, 
        isOfflineData: Boolean
    ) {
        try {
            val data = jobMatchingResponse.data
            
            // Convert to entities
            val jobMatchingEntity = JobMatchingEntity(
                id = data.id,
                dreamJob = data.dreamJob,
                matches = data.matches,
                aiAnalysis = data.aiAnalysis,
                createdAt = data.createdAt,
                totalMatches = data.totalMatches,
                cvSaved = data.cvSaved,
                isSynced = !isOfflineData,
                isOfflineData = isOfflineData
            )

            val jobMatchEntities = data.matches.mapIndexed { index, match ->
                JobMatchEntity(
                    jobMatchingId = data.id,
                    jobId = match.jobId,
                    title = match.title,
                    company = match.company,
                    reasons = match.reasons,
                    strengths = match.strengths,
                    matchScore = match.matchScore,
                    skillMatch = match.skillMatch,
                    locationMatch = match.locationMatch,
                    missingSkills = match.missingSkills,
                    experienceMatch = match.experienceMatch,
                    salaryPotential = match.salaryPotential
                )
            }

            val aiAnalysisEntity = AIAnalysisEntity(
                jobMatchingId = data.id,
                summary = data.aiAnalysis.summary,
                skillGaps = data.aiAnalysis.skillGaps,
                careerPath = data.aiAnalysis.careerPath,
                recommendations = data.aiAnalysis.recommendations,
                dreamJobAlignment = data.aiAnalysis.dreamJobAlignment
            )

            // Save to database
            jobMatchingDao.insertCompleteJobMatching(
                jobMatchingEntity,
                jobMatchEntities,
                aiAnalysisEntity
            )
            
            Log.d(TAG, "Job matching data saved locally: ${data.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving job matching locally", e)
            throw e
        }
    }

    override suspend fun saveOfflineJobMatching(cvFile: File, dreamJob: String): String {
        try {
            // Generate unique ID untuk offline data
            val id = "offline_${System.currentTimeMillis()}"
            val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
            
            // Create dummy response untuk offline data
            val dummyMatches = listOf(
                JobMatch(
                    jobId = "offline_job_1",
                    title = "Menunggu sinkronisasi...",
                    company = "Data akan tersedia setelah sinkronisasi",
                    reasons = listOf("Data sedang diproses offline"),
                    strengths = listOf("Menunggu analisis online"),
                    matchScore = 0,
                    skillMatch = 0,
                    locationMatch = 0,
                    missingSkills = listOf("Analisis akan tersedia online"),
                    experienceMatch = 0,
                    salaryPotential = "Akan dianalisis online"
                )
            )
            
            val dummyAIAnalysis = AIJobAnalysis(
                summary = "Analisis AI akan tersedia setelah sinkronisasi dengan server",
                skillGaps = listOf("Menunggu analisis online"),
                careerPath = "Akan dianalisis setelah online",
                recommendations = listOf("Pastikan koneksi internet stabil untuk analisis lengkap"),
                dreamJobAlignment = "Menunggu analisis AI"
            )

            val jobMatchingEntity = JobMatchingEntity(
                id = id,
                dreamJob = dreamJob,
                matches = dummyMatches,
                aiAnalysis = dummyAIAnalysis,
                createdAt = currentTime,
                totalMatches = 0,
                cvSaved = false,
                isSynced = false,
                cvFilePath = cvFile.absolutePath,
                isOfflineData = true
            )

            val jobMatchEntities = dummyMatches.map { match ->
                JobMatchEntity(
                    jobMatchingId = id,
                    jobId = match.jobId,
                    title = match.title,
                    company = match.company,
                    reasons = match.reasons,
                    strengths = match.strengths,
                    matchScore = match.matchScore,
                    skillMatch = match.skillMatch,
                    locationMatch = match.locationMatch,
                    missingSkills = match.missingSkills,
                    experienceMatch = match.experienceMatch,
                    salaryPotential = match.salaryPotential
                )
            }

            val aiAnalysisEntity = AIAnalysisEntity(
                jobMatchingId = id,
                summary = dummyAIAnalysis.summary,
                skillGaps = dummyAIAnalysis.skillGaps,
                careerPath = dummyAIAnalysis.careerPath,
                recommendations = dummyAIAnalysis.recommendations,
                dreamJobAlignment = dummyAIAnalysis.dreamJobAlignment
            )

            // Save to database
            jobMatchingDao.insertCompleteJobMatching(
                jobMatchingEntity,
                jobMatchEntities,
                aiAnalysisEntity
            )
            
            Log.d(TAG, "Offline job matching saved: $id")
            return id
        } catch (e: Exception) {
            Log.e(TAG, "Error saving offline job matching", e)
            throw e
        }
    }

    override suspend fun getUnsyncedJobMatching(): List<JobMatchingEntity> {
        return jobMatchingDao.getUnsyncedJobMatching()
    }

    override suspend fun markAsSynced(id: String) {
        jobMatchingDao.markAsSynced(id)
        Log.d(TAG, "Marked as synced: $id")
    }

    override suspend fun updateSyncError(id: String, error: String) {
        jobMatchingDao.updateSyncError(id, error)
        Log.d(TAG, "Updated sync error for $id: $error")
    }

    override suspend fun syncPendingData(): List<SyncResult> {
        val results = mutableListOf<SyncResult>()
        val unsyncedData = getUnsyncedJobMatching()
        
        Log.d(TAG, "Syncing ${unsyncedData.size} pending job matching data")
        
        for (entity in unsyncedData) {
            try {
                if (entity.isOfflineData && entity.cvFilePath != null) {
                    // This is offline created data, need to upload to API
                    val cvFile = File(entity.cvFilePath)
                    if (cvFile.exists()) {
                        val syncResult = uploadOfflineData(entity, cvFile)
                        results.add(syncResult)
                    } else {
                        val error = "CV file not found: ${entity.cvFilePath}"
                        updateSyncError(entity.id, error)
                        results.add(SyncResult.Error(entity.id, error))
                    }
                } else {
                    // This is API data that failed to sync before
                    markAsSynced(entity.id)
                    results.add(SyncResult.Success(entity.id))
                }
            } catch (e: Exception) {
                val error = "Sync failed: ${e.localizedMessage}"
                updateSyncError(entity.id, error)
                results.add(SyncResult.Error(entity.id, error))
                Log.e(TAG, "Error syncing ${entity.id}", e)
            }
        }
        
        return results
    }

    private suspend fun uploadOfflineData(entity: JobMatchingEntity, cvFile: File): SyncResult {
        try {
            val token = userRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                throw Exception("Token not found")
            }

            // Prepare multipart data
            val dreamJobBody = entity.dreamJob.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestFile = cvFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            val cvPart = MultipartBody.Part.createFormData("cv", "cv.pdf", requestFile)

            // Upload to API
            val response = apiService.uploadAndMatchJobs(
                authorization = token,
                dreamJob = dreamJobBody,
                cv = cvPart
            )

            if (response.isSuccessful) {
                response.body()?.let { jobMatchingResponse ->
                    // Replace offline data with API response
                    jobMatchingDao.deleteCompleteJobMatching(entity.id)
                    saveJobMatchingLocal(jobMatchingResponse, isOfflineData = false)
                    
                    Log.d(TAG, "Successfully synced offline data: ${entity.id}")
                    return SyncResult.Success(entity.id)
                } ?: throw Exception("Empty response from server")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading offline data", e)
            return SyncResult.Error(entity.id, e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun deleteJobMatching(id: String) {
        jobMatchingDao.deleteCompleteJobMatching(id)
        Log.d(TAG, "Deleted job matching: $id")
    }

    override suspend fun clearAllData() {
        jobMatchingDao.deleteAllJobMatching()
        Log.d(TAG, "Cleared all job matching data")
    }
} 