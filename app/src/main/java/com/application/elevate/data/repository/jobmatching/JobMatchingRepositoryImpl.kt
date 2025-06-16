package com.application.elevate.data.repository.jobmatching

import android.util.Log
import com.application.elevate.api.JobMatchingApiService
import com.application.elevate.data.repository.UserRepository
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
    private val userRepository: UserRepository
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
            
            // Get auth token from UserRepository
            val token = userRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                return JobMatchingResult.Error("Token not found. Please login again.")
            }

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
                authorization = token,
                dreamJob = dreamJobBody,
                cv = cvPart
            )

            if (response.isSuccessful) {
                response.body()?.let { jobMatchingResponse ->
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
            
            // Get auth token from UserRepository
            val token = userRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                return JobMatchingHistoryResult.Error("Token not found. Please login again.")
            }

            val response = apiService.getJobMatchingHistory(authorization = token)

            if (response.isSuccessful) {
                response.body()?.let { historyResponse ->
                    Log.d(TAG, "Job matching history retrieved: ${historyResponse.total} items")
                    JobMatchingHistoryResult.Success(historyResponse)
                } ?: JobMatchingHistoryResult.Error("Empty response from server")
            } else {
                val errorMessage = "Failed to get history: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMessage)
                JobMatchingHistoryResult.Error(errorMessage)
            }

        } catch (e: Exception) {
            val errorMessage = "Error getting history: ${e.localizedMessage}"
            Log.e(TAG, errorMessage, e)
            JobMatchingHistoryResult.Error(errorMessage)
        }
    }
} 