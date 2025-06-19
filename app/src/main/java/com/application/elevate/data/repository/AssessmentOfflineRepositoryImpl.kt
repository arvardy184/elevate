package com.application.elevate.data.repository

import android.util.Log
import com.application.elevate.data.api.AssessmentApiService
import com.application.elevate.data.database.dao.AssessmentDao
import com.application.elevate.data.database.entity.AssessmentEntity
import com.application.elevate.data.database.entity.toAssessmentEntity
import com.application.elevate.data.database.entity.toAssessmentHistory
import com.application.elevate.data.database.entity.toAssessmentRequest
import com.application.elevate.model.AssessmentHistory
import com.application.elevate.model.AssessmentHistoryResponse
import com.application.elevate.model.AssessmentRequest
import com.application.elevate.model.UserResponse
import com.application.elevate.util.NetworkUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class AssessmentOfflineRepositoryImpl @Inject constructor(
    private val api: AssessmentApiService,
    private val userRepository: UserRepository,
    private val assessmentDao: AssessmentDao,
    private val networkUtil: NetworkUtil
) : AssessmentOfflineRepository {
    
    private val TAG = "AssessmentOfflineRepository"
    private val TIMEOUT_DURATION = 30000L

    // Online methods
    override suspend fun submitAssessment(request: AssessmentRequest): Flow<Result<UserResponse>> = flow {
        try {
            val token = userRepository.getToken() ?: throw Exception("Token tidak ditemukan")
            val response = withTimeout(TIMEOUT_DURATION) {
                withContext(Dispatchers.IO) {
                    api.submitAssessment(token, request)
                }
            }
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override suspend fun getAssessmentHistory(): Flow<Result<AssessmentHistoryResponse>> = flow {
        try {
            val token = userRepository.getToken() ?: throw Exception("Token tidak ditemukan")
            val response = withTimeout(TIMEOUT_DURATION) {
                withContext(Dispatchers.IO) {
                    api.getAssessmentHistory(token)
                }
            }
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Local storage methods
    override suspend fun getAssessmentHistoryLocal(userId: Int): List<AssessmentHistory> {
        return try {
            assessmentDao.getAllAssessments(userId).map { it.toAssessmentHistory() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local assessment history: ${e.message}")
            emptyList()
        }
    }
    
    override suspend fun getLatestAssessmentLocal(userId: Int): AssessmentHistory? {
        return try {
            assessmentDao.getLatestAssessment(userId)?.toAssessmentHistory()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting latest local assessment: ${e.message}")
            null
        }
    }
    
    override suspend fun saveAssessmentLocal(request: AssessmentRequest, userId: Int, isSynced: Boolean) {
        try {
            val assessmentEntity = AssessmentEntity(
                id = 0, // Auto-generate
                userId = userId,
                studentStatus = request.studentStatus,
                majorStudy = request.majorStudy,
                currentSemester = request.currentSemester,
                currentField = request.currentField,
                interestedField = request.interestedField,
                dreamJob = request.dreamJob,
                mainGoal = request.mainGoal,
                createdAt = System.currentTimeMillis().toString(),
                isSynced = isSynced,
                lastModified = System.currentTimeMillis(),
                syncError = null
            )
            assessmentDao.insertAssessment(assessmentEntity)
            Log.d(TAG, "Assessment saved locally with sync status: $isSynced")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving local assessment: ${e.message}")
        }
    }
    
    override suspend fun saveAssessmentHistoryLocal(assessments: List<AssessmentHistory>, userId: Int, isSynced: Boolean) {
        try {
            val assessmentEntities = assessments.map { it.toAssessmentEntity(userId, isSynced = isSynced) }
            assessmentDao.insertAllAssessments(assessmentEntities)
            Log.d(TAG, "Assessment history saved locally with sync status: $isSynced")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving local assessment history: ${e.message}")
        }
    }

    // Offline-first methods
    override suspend fun submitAssessmentOfflineFirst(request: AssessmentRequest): Flow<Result<AssessmentHistory>> = flow {
        try {
            Log.d(TAG, "Submitting assessment offline-first: $request")
            val user = userRepository.getUser()
            val userId = user?.id ?: throw Exception("User ID tidak ditemukan")
            
            if (networkUtil.isOnline()) {
                // Online: Try to submit to API first
                Log.d(TAG, "Online: Attempting to submit to API")
                try {
                    val hasExistingAssessment = assessmentDao.hasAssessment(userId)
                    
                    if (!hasExistingAssessment) {
                        // User belum punya assessment, bisa submit via POST
                        submitAssessment(request).collect { result ->
                            result.onSuccess { userResponse ->
                                // API tidak mengembalikan assessment data, buat dummy untuk disimpan
                                val dummyAssessment = createAssessmentFromRequest(request)
                                
                                // Save to local with synced status
                                val assessmentEntity = dummyAssessment.toAssessmentEntity(userId, isSynced = true)
                                assessmentDao.insertAssessment(assessmentEntity)
                                
                                Log.d(TAG, "Assessment submitted online and saved locally")
                                emit(Result.success(dummyAssessment))
                            }.onFailure { error ->
                                Log.w(TAG, "Failed to submit online, saving locally: ${error.message}")
                                // Save locally as unsynced
                                saveAssessmentLocal(request, userId, isSynced = false)
                                val localAssessment = createAssessmentFromRequest(request)
                                emit(Result.success(localAssessment))
                            }
                        }
                    } else {
                        // User sudah punya assessment, API PUT belum tersedia
                        Log.d(TAG, "User already has assessment, PUT API not available yet. Saving locally as unsynced.")
                        saveAssessmentLocal(request, userId, isSynced = false)
                        val localAssessment = createAssessmentFromRequest(request)
                        emit(Result.success(localAssessment))
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "API submit failed, saving locally: ${e.message}")
                    // Save locally as unsynced
                    saveAssessmentLocal(request, userId, isSynced = false)
                    val localAssessment = createAssessmentFromRequest(request)
                    emit(Result.success(localAssessment))
                }
            } else {
                // Offline: Save locally as unsynced
                Log.d(TAG, "Offline: Saving locally as unsynced")
                saveAssessmentLocal(request, userId, isSynced = false)
                val localAssessment = createAssessmentFromRequest(request)
                emit(Result.success(localAssessment))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in submitAssessmentOfflineFirst: ${e.message}")
            emit(Result.failure(e))
        }
    }
    
    override suspend fun getAssessmentHistoryOfflineFirst(userId: Int): Flow<Result<List<AssessmentHistory>>> = flow {
        try {
            Log.d(TAG, "Getting assessment history offline-first for user: $userId")
            
            if (networkUtil.isOnline()) {
                // Online: Try to get from API first
                Log.d(TAG, "Online: Fetching from API")
                try {
                    getAssessmentHistory().collect { result ->
                        result.onSuccess { historyResponse ->
                            // Save to local database
                            saveAssessmentHistoryLocal(historyResponse.data, userId, isSynced = true)
                            
                            Log.d(TAG, "Assessment history fetched from API and saved locally")
                            emit(Result.success(historyResponse.data))
                        }.onFailure { error ->
                            Log.w(TAG, "Failed to fetch from API, falling back to local: ${error.message}")
                            // Fall through to local fetch
                            val localAssessments = getAssessmentHistoryLocal(userId)
                            emit(Result.success(localAssessments))
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch from API, falling back to local: ${e.message}")
                    // Fall through to local fetch
                    val localAssessments = getAssessmentHistoryLocal(userId)
                    emit(Result.success(localAssessments))
                }
            } else {
                // Offline: Get from local database
                Log.d(TAG, "Offline: Fetching from local database")
                val localAssessments = getAssessmentHistoryLocal(userId)
                emit(Result.success(localAssessments))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in getAssessmentHistoryOfflineFirst: ${e.message}")
            emit(Result.failure(e))
        }
    }
    
    override suspend fun getLatestAssessmentOfflineFirst(userId: Int): Flow<Result<AssessmentHistory?>> = flow {
        try {
            Log.d(TAG, "Getting latest assessment offline-first for user: $userId")
            
            if (networkUtil.isOnline()) {
                // Online: Try to get from API first
                Log.d(TAG, "Online: Fetching from API")
                try {
                    getAssessmentHistory().collect { result ->
                        result.onSuccess { historyResponse ->
                            // Save to local database
                            saveAssessmentHistoryLocal(historyResponse.data, userId, isSynced = true)
                            
                            val latestAssessment = historyResponse.data.maxByOrNull { it.id }
                            Log.d(TAG, "Latest assessment fetched from API and saved locally")
                            emit(Result.success(latestAssessment))
                        }.onFailure { error ->
                            Log.w(TAG, "Failed to fetch from API, falling back to local: ${error.message}")
                            // Fall through to local fetch
                            val localAssessment = getLatestAssessmentLocal(userId)
                            emit(Result.success(localAssessment))
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch from API, falling back to local: ${e.message}")
                    // Fall through to local fetch
                    val localAssessment = getLatestAssessmentLocal(userId)
                    emit(Result.success(localAssessment))
                }
            } else {
                // Offline: Get from local database
                Log.d(TAG, "Offline: Fetching from local database")
                val localAssessment = getLatestAssessmentLocal(userId)
                emit(Result.success(localAssessment))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in getLatestAssessmentOfflineFirst: ${e.message}")
            emit(Result.failure(e))
        }
    }

    // Sync methods
    override suspend fun syncAssessments(): Flow<Result<Boolean>> = flow {
        try {
            if (!networkUtil.isOnline()) {
                Log.d(TAG, "Device offline, cannot sync")
                emit(Result.failure(Exception("Device offline")))
                return@flow
            }
            
            Log.d(TAG, "Starting assessment sync...")
            val unsyncedAssessments = assessmentDao.getUnsyncedAssessments()
            var syncedCount = 0
            
            for (assessmentEntity in unsyncedAssessments) {
                try {
                    Log.d(TAG, "Syncing assessment: ${assessmentEntity.id}")
                    val request = assessmentEntity.toAssessmentRequest()
                    val hasExistingAssessment = assessmentDao.hasAssessment(assessmentEntity.userId)
                    
                    if (!hasExistingAssessment || assessmentEntity.id == 0) {
                        // Submit new assessment
                        submitAssessment(request).collect { result ->
                            result.onSuccess { response ->
                                // Update local with synced status
                                assessmentDao.markAsSynced(assessmentEntity.id)
                                syncedCount++
                                Log.d(TAG, "Assessment ${assessmentEntity.id} synced successfully")
                            }.onFailure { error ->
                                // Mark with sync error
                                assessmentDao.markAsUnsynced(assessmentEntity.id, error.message)
                                Log.e(TAG, "Failed to sync assessment ${assessmentEntity.id}: ${error.message}")
                            }
                        }
                    } else {
                        // PUT API not available yet, mark as error
                        assessmentDao.markAsUnsynced(assessmentEntity.id, "PUT API not available yet")
                        Log.d(TAG, "Assessment ${assessmentEntity.id} requires PUT API (not available yet)")
                    }
                    
                } catch (e: Exception) {
                    assessmentDao.markAsUnsynced(assessmentEntity.id, e.message)
                    Log.e(TAG, "Exception syncing assessment ${assessmentEntity.id}: ${e.message}")
                }
            }
            
            Log.d(TAG, "Sync completed: $syncedCount/${unsyncedAssessments.size} assessments synced")
            emit(Result.success(syncedCount > 0))
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in syncAssessments: ${e.message}")
            emit(Result.failure(e))
        }
    }
    
    override suspend fun syncUnsyncedAssessments(): Flow<Result<Int>> = flow {
        try {
            if (!networkUtil.isOnline()) {
                emit(Result.failure(Exception("Device offline")))
                return@flow
            }
            
            val unsyncedAssessments = assessmentDao.getUnsyncedAssessments()
            var syncedCount = 0
            
            for (assessmentEntity in unsyncedAssessments) {
                try {
                    val request = assessmentEntity.toAssessmentRequest()
                    val hasExistingAssessment = assessmentDao.hasAssessment(assessmentEntity.userId)
                    
                    if (!hasExistingAssessment || assessmentEntity.id == 0) {
                        submitAssessment(request).collect { result ->
                            result.onSuccess { response ->
                                assessmentDao.markAsSynced(assessmentEntity.id)
                                syncedCount++
                            }.onFailure { error ->
                                assessmentDao.markAsUnsynced(assessmentEntity.id, error.message)
                            }
                        }
                    } else {
                        // PUT API not available yet
                        assessmentDao.markAsUnsynced(assessmentEntity.id, "PUT API not available yet")
                    }
                } catch (e: Exception) {
                    assessmentDao.markAsUnsynced(assessmentEntity.id, e.message)
                }
            }
            
            emit(Result.success(syncedCount))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Helper methods
    private fun createAssessmentFromRequest(request: AssessmentRequest): AssessmentHistory {
        return AssessmentHistory(
            id = System.currentTimeMillis().toInt(), // Temporary ID
            studentStatus = request.studentStatus,
            majorStudy = request.majorStudy,
            currentSemester = request.currentSemester,
            currentField = request.currentField,
            interestedField = request.interestedField,
            dreamJob = request.dreamJob,
            mainGoal = request.mainGoal,
            createdAt = System.currentTimeMillis().toString()
        )
    }
    


    // Utility methods
    override suspend fun hasUserAssessment(userId: Int): Boolean {
        return try {
            assessmentDao.hasAssessment(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if user has assessment: ${e.message}")
            false
        }
    }
} 