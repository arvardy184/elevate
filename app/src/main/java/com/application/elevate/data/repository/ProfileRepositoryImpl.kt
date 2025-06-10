package com.application.elevate.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.application.elevate.data.api.ProfileApiService
import com.application.elevate.model.ProfileResponse
import com.application.elevate.model.User
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
    private val context: Context
) : ProfileRepository {
    private val TAG = "ProfileRepository"
    private val TIMEOUT_DURATION = 180000L // 180 detik timeout untuk upload file besar

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    override suspend fun updateProfile(user: User): Flow<Result<ProfileResponse>> = flow {
        var retryCount = 0
        val maxRetries = 3
        var lastError: Exception? = null

        while (retryCount < maxRetries) {
            try {
                Log.d(TAG, "Mencoba update profil (percobaan ${retryCount + 1}): $user")

                val token = userRepository.getToken() ?: throw Exception("Token tidak ditemukan")

                // Membuat multipart jika ada foto
                val profilePicturePart = user.photoUrl?.let { photoUrl ->
                    if (photoUrl.isNotEmpty() && photoUrl != "null" && !photoUrl.startsWith("http")) {
                        createMultipartFromUri(photoUrl) ?: throw Exception("Gagal membuat multipart")
                    } else null
                }

                // Membuat RequestBody untuk setiap field
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

                // Update user di repository lokal
                userRepository.updateUser(response.user)
                emit(Result.success(response))
                break

            } catch (e: Exception) {
                lastError = e
                Log.e(TAG, "Error pada percobaan ${retryCount + 1}: ${e.message}")

                when (e) {
                    is TimeoutCancellationException,
                    is IOException,
                    is HttpException,
                    is java.net.SocketException -> {
                        retryCount++
                        if (retryCount < maxRetries) {
                            val delayTime = 2000L * retryCount
                            Log.d(TAG, "Menunggu ${delayTime}ms sebelum mencoba lagi...")
                            delay(delayTime)
                            continue
                        }
                    }
                    else -> {
                        Log.e(TAG, "Error fatal: ${e.message}")
                        break
                    }
                }
            }
        }

        if (lastError != null) {
            emit(Result.failure(lastError))
        }
    }.catch { e ->
        Log.e(TAG, "Error tidak tertangani: ${e.message}")
        emit(Result.failure(e))
    }
}