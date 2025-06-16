package com.application.elevate.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.application.elevate.data.api.ProfileApiService
import com.application.elevate.data.database.dao.ProfileDao
import com.application.elevate.data.database.entity.ProfileEntity
import com.application.elevate.data.database.entity.toProfileEntity
import com.application.elevate.data.database.entity.toUser
import com.application.elevate.model.ProfileResponse
import com.application.elevate.model.User
import com.application.elevate.util.NetworkUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApiService,
    private val userRepository: UserRepository,
    private val profileDao: ProfileDao,
    private val networkUtil: NetworkUtil,
    private val context: Context
) : ProfileRepository {
    private val TAG = "ProfileRepository"
    private val TIMEOUT_DURATION = 180000L // 180 detik timeout untuk upload file besar

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ========== OFFLINE-FIRST METHODS ==========
    
    override suspend fun getProfileOfflineFirst(userId: Int): Flow<Result<User>> = flow {
        try {
            Log.d(TAG, "Getting profile offline-first for user: $userId")
            
            if (networkUtil.isOnline()) {
                // Online: Try to get from API first
                Log.d(TAG, "Online: Fetching from API")
                try {
                    val token = userRepository.getToken() ?: throw Exception("Token tidak ditemukan")
                    val response = withTimeout(TIMEOUT_DURATION) {
                        withContext(Dispatchers.IO) {
                            api.getProfile(token)
                        }
                    }
                    
                    // Save to local database
                    val profileEntity = response.user.toProfileEntity(isSynced = true)
                    profileDao.insertProfile(profileEntity)
                    
                    Log.d(TAG, "Profile fetched from API and saved locally")
                    emit(Result.success(response.user))
                    return@flow
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch from API, falling back to local: ${e.message}")
                    // Fall through to local fetch
                }
            }
            
            // Offline or API failed: Get from local database
            Log.d(TAG, "Fetching from local database")
            val localProfile = profileDao.getProfile(userId)
            if (localProfile != null) {
                Log.d(TAG, "Profile found in local database")
                emit(Result.success(localProfile.toUser()))
            } else {
                Log.e(TAG, "No profile found in local database")
                emit(Result.failure(Exception("No profile data available")))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in getProfileOfflineFirst: ${e.message}")
            emit(Result.failure(e))
        }
    }.catch { e ->
        Log.e(TAG, "Unexpected error in getProfileOfflineFirst: ${e.message}")
        emit(Result.failure(e))
    }
    
    override suspend fun updateProfileOfflineFirst(user: User): Flow<Result<User>> = flow {
        try {
            Log.d(TAG, "Updating profile offline-first: $user")
            val userId = user.id ?: 0
            
            if (networkUtil.isOnline()) {
                // Online: Try to update API first
                Log.d(TAG, "Online: Updating via API")
                try {
                    val response = updateProfileOnline(user)
                    response.collect { result ->
                        result.onSuccess { profileResponse ->
                            // Save to local with synced status
                            val profileEntity = profileResponse.user.toProfileEntity(isSynced = true)
                            profileDao.insertProfile(profileEntity)
                            
                            Log.d(TAG, "Profile updated online and saved locally")
                            emit(Result.success(profileResponse.user))
                        }.onFailure { error ->
                            Log.w(TAG, "Failed to update online, saving locally: ${error.message}")
                            // Save locally as unsynced
                            val profileEntity = user.toProfileEntity(isSynced = false)
                            profileDao.insertProfile(profileEntity)
                            emit(Result.success(user))
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "API update failed, saving locally: ${e.message}")
                    // Save locally as unsynced
                    val profileEntity = user.toProfileEntity(isSynced = false)
                    profileDao.insertProfile(profileEntity)
                    emit(Result.success(user))
                }
            } else {
                // Offline: Save locally as unsynced
                Log.d(TAG, "Offline: Saving locally as unsynced")
                val profileEntity = user.toProfileEntity(isSynced = false)
                profileDao.insertProfile(profileEntity)
                emit(Result.success(user))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateProfileOfflineFirst: ${e.message}")
            emit(Result.failure(e))
        }
    }.catch { e ->
        Log.e(TAG, "Unexpected error in updateProfileOfflineFirst: ${e.message}")
        emit(Result.failure(e))
    }
    
    // ========== LOCAL STORAGE METHODS ==========
    
    override suspend fun getProfileLocal(userId: Int): User? {
        return try {
            profileDao.getProfile(userId)?.toUser()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local profile: ${e.message}")
            null
        }
    }
    
    override suspend fun saveProfileLocal(user: User, isSynced: Boolean) {
        try {
            val profileEntity = user.toProfileEntity(isSynced = isSynced)
            profileDao.insertProfile(profileEntity)
            Log.d(TAG, "Profile saved locally with sync status: $isSynced")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving local profile: ${e.message}")
        }
    }
    
    // ========== SYNC METHODS ==========
    
    override suspend fun syncProfile(): Flow<Result<Boolean>> = flow {
        try {
            if (!networkUtil.isOnline()) {
                Log.d(TAG, "Device offline, cannot sync")
                emit(Result.failure(Exception("Device offline")))
                return@flow
            }
            
            Log.d(TAG, "Starting profile sync...")
            val unsyncedProfiles = profileDao.getUnsyncedProfiles()
            var syncedCount = 0
            
            for (profileEntity in unsyncedProfiles) {
                try {
                    Log.d(TAG, "Syncing profile: ${profileEntity.id}")
                    val user = profileEntity.toUser()
                    
                    updateProfileOnline(user).collect { result ->
                        result.onSuccess { response ->
                            // Update local with synced data
                            val syncedEntity = response.user.toProfileEntity(isSynced = true)
                            profileDao.insertProfile(syncedEntity)
                            syncedCount++
                            Log.d(TAG, "Profile ${profileEntity.id} synced successfully")
                        }.onFailure { error ->
                            // Mark with sync error
                            profileDao.markAsUnsynced(profileEntity.id, error.message)
                            Log.e(TAG, "Failed to sync profile ${profileEntity.id}: ${error.message}")
                        }
                    }
                } catch (e: Exception) {
                    profileDao.markAsUnsynced(profileEntity.id, e.message)
                    Log.e(TAG, "Exception syncing profile ${profileEntity.id}: ${e.message}")
                }
            }
            
            Log.d(TAG, "Sync completed: $syncedCount/${unsyncedProfiles.size} profiles synced")
            emit(Result.success(syncedCount == unsyncedProfiles.size))
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in syncProfile: ${e.message}")
            emit(Result.failure(e))
        }
    }.catch { e ->
        Log.e(TAG, "Unexpected error in syncProfile: ${e.message}")
        emit(Result.failure(e))
    }
    
    override suspend fun syncUnsyncedProfiles(): Flow<Result<Int>> = flow {
        try {
            if (!networkUtil.isOnline()) {
                emit(Result.failure(Exception("Device offline")))
                return@flow
            }
            
            val unsyncedProfiles = profileDao.getUnsyncedProfiles()
            var syncedCount = 0
            
            for (profileEntity in unsyncedProfiles) {
                try {
                    val user = profileEntity.toUser()
                    updateProfileOnline(user).collect { result ->
                        result.onSuccess { response ->
                            val syncedEntity = response.user.toProfileEntity(isSynced = true)
                            profileDao.insertProfile(syncedEntity)
                            syncedCount++
                        }.onFailure { error ->
                            profileDao.markAsUnsynced(profileEntity.id, error.message)
                        }
                    }
                } catch (e: Exception) {
                    profileDao.markAsUnsynced(profileEntity.id, e.message)
                }
            }
            
            emit(Result.success(syncedCount))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    // ========== EXISTING METHODS (Updated) ==========
    
    private fun createRequestBody(value: String?): okhttp3.RequestBody {
        return okhttp3.RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            value ?: ""
        )
    }

    private fun createMultipartFromUri(uri: String): MultipartBody.Part? {
        return try {
            Log.d(TAG, "Mencoba membuat multipart dari URI: $uri")
            val contentUri = Uri.parse(uri)
            val contentResolver = context.contentResolver

            // Mendapatkan mime type
            val mimeType = contentResolver.getType(contentUri) ?: "image/jpeg"
            Log.d(TAG, "Mime type: $mimeType")

            // Membuat file temporary dengan ekstensi yang benar
            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType) ?: "jpg"

            val tempFile = File.createTempFile(
                "profile_picture",
                ".$extension",
                context.cacheDir
            ).apply {
                deleteOnExit() // Hapus file sementara setelah selesai
            }

            Log.d(TAG, "Temporary file created: ${tempFile.absolutePath}")

            // Copy file dari URI ke temporary file dengan buffer yang lebih besar
            contentResolver.openInputStream(contentUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8192) // 8KB buffer
                    var read: Int
                    var totalBytesRead = 0L
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        totalBytesRead += read
                        Log.d(TAG, "Progress: $totalBytesRead bytes copied")
                    }
                    output.flush()
                }
            }

            Log.d(TAG, "File size: ${tempFile.length()} bytes")

            // Membuat MultipartBody.Part
            val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData(
                "profilePicture",
                "profile_picture.$extension",
                requestFile
            )

            Log.d(TAG, "Multipart created successfully")
            part
        } catch (e: Exception) {
            Log.e(TAG, "Error saat membuat multipart: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTrace.joinToString("\n")}")
            null
        }
    }

    override suspend fun getProfile(): Flow<Result<ProfileResponse>> = flow {
        try {
            Log.d(TAG, "Mencoba mendapatkan data profil")
            val token = withContext(Dispatchers.IO) {
                userRepository.getToken() ?: throw Exception("Token tidak ditemukan")
            }
            Log.d(TAG, "Token yang digunakan: $token")

            val response = withTimeout(TIMEOUT_DURATION) {
                withContext(Dispatchers.IO) {
                    api.getProfile(token)
                }
            }
            Log.d(TAG, "Berhasil mendapatkan data profil: $response")
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error saat mendapatkan profil: ${e.message}")
            emit(Result.failure(e))
        }
    }.catch { e ->
        Log.e(TAG, "Unexpected error saat mendapatkan profil: ${e.message}")
        emit(Result.failure(e))
    }

    private suspend fun updateProfileOnline(user: User): Flow<Result<ProfileResponse>> = flow {
        var retryCount = 0
        val maxRetries = 3

        while (retryCount < maxRetries) {
            try {
                if (user == null) {
                    Log.e(TAG, "Error: User object is null")
                    emit(Result.failure(Exception("User data tidak valid")))
                    return@flow
                }

                Log.d(TAG, "Mencoba update profil (percobaan ${retryCount + 1}): $user")

                val token = userRepository.getToken() ?: throw Exception("Token tidak ditemukan")

                // Mendapatkan user data yang ada
                val currentUser = userRepository.getUser()
                
                // Membuat multipart hanya jika ada perubahan foto profil
                val profilePicturePart = if (user.photoUrl != currentUser?.photoUrl) {
                    try {
                        user.photoUrl?.let { photoUrl ->
                            if (photoUrl.isNotEmpty() && photoUrl != "null" && !photoUrl.startsWith("http")) {
                                createMultipartFromUri(photoUrl)
                            } else null
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error saat membuat multipart: ${e.message}")
                        null
                    }
                } else {
                    // Jika tidak ada perubahan foto, gunakan foto yang ada
                    Log.d(TAG, "Tidak ada perubahan foto profil, menggunakan foto yang ada")
                    null
                }

                // Membuat RequestBody untuk setiap field dengan pengecekan null
                val firstNameBody = createRequestBody(user.firstName)
                val lastNameBody = createRequestBody(user.lastName)
                val addressBody = createRequestBody(user.address)
                val phoneNumberBody = createRequestBody(user.phoneNumber)
                val genderBody = createRequestBody(user.gender)
                val birthDateBody = createRequestBody(user.birthDate)

                Log.d(TAG, "Mengirim request dengan data: firstName=${user.firstName}, lastName=${user.lastName}, address=${user.address}, phoneNumber=${user.phoneNumber}, gender=${user.gender}, birthDate=${user.birthDate}")

                val response = withContext(Dispatchers.IO) {
                    api.updateProfile(
                        token = token,
                        firstName = firstNameBody,
                        lastName = lastNameBody,
                        address = addressBody,
                        phoneNumber = phoneNumberBody,
                        gender = genderBody,
                        birthDate = birthDateBody,
                        profilePicture = profilePicturePart
                    )
                }

                Log.d(TAG, "Update profile berhasil")

                // Jika response.user null tapi update berhasil, gunakan data user yang dikirim
                val updatedUser = if (response.user == null) {
                    Log.d(TAG, "Response user null, menggunakan data user yang dikirim")
                    user.copy(
                        photoUrl = currentUser?.photoUrl ?: user.photoUrl,
                        isAssessmentCompleted = currentUser?.isAssessmentCompleted ?: false
                    )
                } else {
                    // Jika ada response user, gunakan data dari response tapi pertahankan foto jika tidak ada perubahan
                    if (profilePicturePart == null && currentUser?.photoUrl != null) {
                        response.user.copy(photoUrl = currentUser.photoUrl)
                    } else {
                        response.user
                    }
                }

                // Update user di repository lokal
                userRepository.updateUser(updatedUser)
                
                // Emit success dengan user yang sudah diupdate
                emit(Result.success(ProfileResponse(
                    message = response.message,
                    user = updatedUser
                )))
                break

            } catch (e: Exception) {
                retryCount++
                Log.e(TAG, "Error pada percobaan $retryCount: ${e.message}")
                if (retryCount == maxRetries) {
                    Log.e(TAG, "Error fatal: ${e.message}")
                    emit(Result.failure(e))
                } else {
                    delay(1000) // Tunggu 1 detik sebelum mencoba lagi
                }
            }
        }
    }.catch { e ->
        Log.e(TAG, "Unexpected error saat update profil: ${e.message}")
        emit(Result.failure(e))
    }

    override suspend fun updateProfile(user: User): Flow<Result<ProfileResponse>> {
        return updateProfileOnline(user)
    }
}