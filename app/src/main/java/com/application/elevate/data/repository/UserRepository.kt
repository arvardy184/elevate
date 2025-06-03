package com.application.elevate.data.repository

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.application.elevate.model.User
import com.application.elevate.data.datastore.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Singleton
class UserRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    private val TAG = "UserRepository"
    
    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow: Flow<User?> = _userFlow.asStateFlow()
    
    private var _token: String? = null
    
    // Membuat CoroutineScope yang aman
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    init {
        // Muat data user dari DataStore saat inisialisasi
        loadUserFromDataStore()
    }
    
    private fun loadUserFromDataStore() {
        repositoryScope.launch {
            try {
                val preferences = dataStoreManager.dataStore.data.first()
                val user = User(
                    id = preferences[USER_ID_KEY]?.toIntOrNull() ?: 0,
                    firstName = preferences[USER_FIRST_NAME_KEY] ?: "",
                    lastName = preferences[USER_LAST_NAME_KEY] ?: "",
                    email = preferences[USER_EMAIL_KEY] ?: "",
                    photoUrl = preferences[USER_AVATAR_KEY] ?: "",
                    address = preferences[USER_ADDRESS_KEY] ?: "",
                    phoneNumber = preferences[USER_PHONE_KEY] ?: "",
                    gender = preferences[USER_GENDER_KEY] ?: "",
                    birthDate = preferences[USER_BIRTH_DATE_KEY] ?: "",
                    role = preferences[USER_ROLE_KEY] ?: "USER",
                    isAssessmentCompleted = preferences[USER_IS_ASSESSMENT_COMPLETED_KEY] ?: false
                )
                _userFlow.value = user
                Log.d(TAG, "Data user berhasil dimuat dari DataStore: $user")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user from DataStore: ${e.message}")
            }
        }
    }
    
    // Flow untuk memantau perubahan token
    val tokenFlow: Flow<String?> = dataStoreManager.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // Flow untuk memantau status remember me
    val rememberMeFlow: Flow<Boolean> = dataStoreManager.dataStore.data.map { preferences ->
        preferences[REMEMBER_ME_KEY] ?: false
    }
    
    suspend fun setToken(token: String?) {
        try {
            Log.d(TAG, "Mencoba menyimpan token autentikasi untuk remember me")
            
            if (token.isNullOrBlank()) {
                Log.e(TAG, "Token autentikasi kosong atau null")
                throw IllegalArgumentException("Token tidak boleh null atau kosong")
            }
            
            // Pastikan token dalam format yang benar (Bearer token)
            val formattedToken = if (token.startsWith("Bearer ")) {
                token
            } else {
                "Bearer $token"
            }
            
            Log.d(TAG, "Menyimpan token dan mengaktifkan remember me")
            dataStoreManager.dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = formattedToken
                preferences[REMEMBER_ME_KEY] = true
            }
            Log.d(TAG, "Token autentikasi berhasil disimpan untuk remember me")
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan token autentikasi: ${e.message}")
            throw Exception("Gagal menyimpan token: ${e.message}")
        }
    }

    suspend fun setRememberMe(remember: Boolean) {
        try {
            Log.d(TAG, "Mengatur status remember me: $remember")
            dataStoreManager.dataStore.edit { preferences ->
                preferences[REMEMBER_ME_KEY] = remember
            }
            Log.d(TAG, "Status remember me berhasil diatur: $remember")
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengatur status remember me: ${e.message}")
            throw Exception("Gagal mengatur remember me: ${e.message}")
        }
    }
    
    // Fungsi untuk mendapatkan token dengan retry
    suspend fun getToken(): String? {
        var retryCount = 0
        val maxRetries = 3
        var lastError: Exception? = null

        while (retryCount < maxRetries) {
            try {
                Log.d(TAG, "Mencoba mendapatkan token autentikasi untuk remember me")
                val preferences = dataStoreManager.dataStore.data.first()
                val token = preferences[TOKEN_KEY]
                
                if (token != null) {
                    Log.d(TAG, "Token autentikasi ditemukan")
                    return token
                } else {
                    Log.d(TAG, "Token tidak ditemukan")
                    return null
                }
            } catch (e: Exception) {
                lastError = e
                Log.e(TAG, "Gagal mendapatkan token autentikasi: ${e.message}")
                
                if (e.message?.contains("Job was cancelled") == true || 
                    e.message?.contains("Socket closed") == true) {
                    retryCount++
                    if (retryCount < maxRetries) {
                        Log.d(TAG, "Mencoba mendapatkan token lagi (percobaan $retryCount)")
                        kotlinx.coroutines.delay(1000L * retryCount) // Exponential backoff
                        continue
                    }
                }
                break
            }
        }
        
        throw lastError ?: Exception("Gagal mendapatkan token setelah $maxRetries percobaan")
    }
    //TumbuhNyata was here
    private fun isTokenValid(token: String): Boolean {
        return try {
            Log.d(TAG, "Memvalidasi token JWT untuk remember me")
            
            // Hapus prefix Bearer jika ada
            val cleanToken = if (token.startsWith("Bearer ")) {
                token.substring(7)
            } else {
                token
            }
            
            // Validasi format token JWT
            if (!cleanToken.matches(Regex("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"))) {
                Log.e(TAG, "Format token JWT tidak valid: $cleanToken")
                return false
            }

            // Decode payload JWT
            val parts = cleanToken.split(".")
            if (parts.size != 3) {
                Log.e(TAG, "Token JWT tidak memiliki 3 bagian")
                return false
            }

            // Decode payload (bagian kedua)
            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT))
            val jsonObject = org.json.JSONObject(payload)

            // Cek expired time
            val exp = jsonObject.optLong("exp", 0)
            val currentTime = System.currentTimeMillis() / 1000

            if (exp <= currentTime) {
                Log.e(TAG, "Token JWT sudah expired")
                return false
            }
            
            Log.d(TAG, "Token JWT valid untuk remember me")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saat validasi token JWT: ${e.message}")
            false
        }
    }
    
    // Fungsi untuk mengecek apakah user sudah login
    suspend fun isLoggedIn(): Boolean {
        try {
            Log.d(TAG, "Mengecek status login dengan token remember me")
            val preferences = dataStoreManager.dataStore.data.first()
            val token = preferences[TOKEN_KEY]
            val rememberMe = preferences[REMEMBER_ME_KEY] ?: false
            
            if (token == null || !rememberMe) {
                Log.d(TAG, "Token remember me tidak ditemukan atau remember me tidak aktif")
                return false
            }
            
            // Validasi token dan sinkronisasi data
            if (isTokenValid(token)) {
                Log.d(TAG, "Token remember me valid, melakukan sinkronisasi data")
                try {
                    // Ambil data user dari DataStore
                    val user = getUser()
                    if (user != null) {
                        // Update status assessment dari DataStore
                        val assessmentStatus = preferences[USER_IS_ASSESSMENT_COMPLETED_KEY] ?: false
                        Log.d(TAG, "Status assessment dari DataStore: $assessmentStatus")
                        
                        // Update user flow dengan status assessment yang benar
                        _userFlow.value = user.copy(isAssessmentCompleted = assessmentStatus)
                        Log.d(TAG, "User flow diupdate dengan status assessment: $assessmentStatus")
                    }
                    return true
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal sinkronisasi data: ${e.message}")
                    return true
                }
            }
            
            Log.d(TAG, "Token remember me tidak valid")
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error saat mengecek status login dengan token remember me: ${e.message}")
            return false
        }
    }

    private suspend fun syncUserDataFromServer() {
        try {
            Log.d(TAG, "Memulai sinkronisasi data user dari server")
            // Ambil data user dari server menggunakan API
            val user = getUser()
            if (user != null) {
                // Update data di DataStore
                dataStoreManager.dataStore.edit { prefs ->
                    prefs[USER_ID_KEY] = user.id.toString()
                    prefs[USER_FIRST_NAME_KEY] = user.firstName
                    prefs[USER_LAST_NAME_KEY] = user.lastName
                    prefs[USER_EMAIL_KEY] = user.email
                    prefs[USER_AVATAR_KEY] = user.getPhotoUrlOrDefault()
                    prefs[USER_ADDRESS_KEY] = user.getAddressOrDefault()
                    prefs[USER_PHONE_KEY] = user.getPhoneNumberOrDefault()
                    prefs[USER_GENDER_KEY] = user.getGenderOrDefault()
                    prefs[USER_BIRTH_DATE_KEY] = user.getBirthDateOrDefault()
                    prefs[USER_ROLE_KEY] = user.role
                    prefs[USER_IS_ASSESSMENT_COMPLETED_KEY] = user.isAssessmentCompleted
                }
                
                // Update user flow
                _userFlow.value = user
                
                Log.d(TAG, "Data user berhasil disinkronkan dari server")
                Log.d(TAG, "Status assessment terbaru: ${user.isAssessmentCompleted}")
            } else {
                Log.e(TAG, "Data user tidak ditemukan di server")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saat sinkronisasi data user: ${e.message}")
            throw e
        }
    }

    suspend fun updateUser(user: User) {
        Log.d(TAG, "Memperbarui data user: $user")
        Log.d(TAG, "Status assessment sebelum update: ${_userFlow.value?.isAssessmentCompleted}")
        
        // Ambil status assessment yang ada di DataStore
        val currentAssessmentStatus = dataStoreManager.dataStore.data.first()[USER_IS_ASSESSMENT_COMPLETED_KEY] ?: false
        Log.d(TAG, "Status assessment dari DataStore: $currentAssessmentStatus")
        
        // Update user flow dengan mempertahankan status assessment
        _userFlow.value = user.copy(
            isAssessmentCompleted = currentAssessmentStatus,
            photoUrl = user.getPhotoUrlOrDefault(),
            address = user.getAddressOrDefault(),
            phoneNumber = user.getPhoneNumberOrDefault(),
            gender = user.getGenderOrDefault(),
            birthDate = user.getBirthDateOrDefault()
        )
        
        try {
            // Simpan data user ke DataStore dengan mempertahankan status assessment
            dataStoreManager.dataStore.edit { preferences ->
                preferences[USER_ID_KEY] = user.id.toString()
                preferences[USER_FIRST_NAME_KEY] = user.firstName
                preferences[USER_LAST_NAME_KEY] = user.lastName
                preferences[USER_EMAIL_KEY] = user.email
                preferences[USER_AVATAR_KEY] = user.getPhotoUrlOrDefault()
                preferences[USER_ADDRESS_KEY] = user.getAddressOrDefault()
                preferences[USER_PHONE_KEY] = user.getPhoneNumberOrDefault()
                preferences[USER_GENDER_KEY] = user.getGenderOrDefault()
                preferences[USER_BIRTH_DATE_KEY] = user.getBirthDateOrDefault()
                preferences[USER_ROLE_KEY] = user.role
                // Tetap gunakan status assessment yang ada di DataStore
                preferences[USER_IS_ASSESSMENT_COMPLETED_KEY] = currentAssessmentStatus
            }
            Log.d(TAG, "Data user berhasil disimpan ke DataStore")
            Log.d(TAG, "Status assessment setelah update di DataStore: $currentAssessmentStatus")
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan data user ke DataStore: ${e.message}")
        }
        
        Log.d(TAG, "Status assessment setelah update di userFlow: ${_userFlow.value?.isAssessmentCompleted}")
    }

    // Fungsi untuk update user secara synchronous
    fun updateUserSync(user: User) {
        _userFlow.value = user
        Log.d(TAG, "Data user berhasil diupdate secara synchronous: $user")
    }

    fun getUser(): User? {
        val user = _userFlow.value
        Log.d(TAG, "Mendapatkan data user: $user")
        Log.d(TAG, "Nama depan: ${user?.firstName}, Nama belakang: ${user?.lastName}")
        Log.d(TAG, "Nama lengkap: ${user?.fullName}")
        Log.d(TAG, "Status assessment saat get user: ${user?.isAssessmentCompleted}")
        return user
    }

    suspend fun clearUser() {
        Log.d(TAG, "Membersihkan data user dan token remember me")
        _userFlow.value = null
        
        dataStoreManager.dataStore.edit { prefs ->
            // Hapus semua data termasuk token remember me
            prefs.remove(TOKEN_KEY)
            prefs.remove(REMEMBER_ME_KEY)
            prefs.remove(USER_ID_KEY)
            prefs.remove(USER_FIRST_NAME_KEY)
            prefs.remove(USER_LAST_NAME_KEY)
            prefs.remove(USER_EMAIL_KEY)
            prefs.remove(USER_PHONE_KEY)
            prefs.remove(USER_AVATAR_KEY)
            prefs.remove(USER_BIRTH_DATE_KEY)
            prefs.remove(USER_GENDER_KEY)
            prefs.remove(USER_ADDRESS_KEY)
            prefs.remove(USER_ROLE_KEY)
            prefs.remove(USER_IS_ASSESSMENT_COMPLETED_KEY)
        }
        Log.d(TAG, "Data user dan token remember me berhasil dibersihkan")
    }
    
    // Fungsi khusus untuk mendapatkan token dalam format yang benar untuk API
    suspend fun getAuthToken(): String? {
        Log.d(TAG, "Mendapatkan token autentikasi untuk API request")
        val token = getToken()
        Log.d(TAG, if (token != null) "Token autentikasi berhasil didapatkan untuk API" else "Token autentikasi tidak ditemukan untuk API")
        return token
    }

    suspend fun isFirstLaunch(): Boolean {
        val preferences = dataStoreManager.dataStore.data.first()
        return preferences[IS_FIRST_LAUNCH_KEY] ?: true
    }

    suspend fun setFirstLaunch(isFirst: Boolean) {
        dataStoreManager.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH_KEY] = isFirst
        }
    }

    // Tambahkan fungsi untuk mendapatkan status assessment dari DataStore
    suspend fun getAssessmentStatus(): Boolean {
        val preferences = dataStoreManager.dataStore.data.first()
        val status = preferences[USER_IS_ASSESSMENT_COMPLETED_KEY] ?: false
        Log.d(TAG, "Status assessment dari DataStore: $status")
        return status
    }

    suspend fun shouldShowTutorial(): Boolean {
        val preferences = dataStoreManager.dataStore.data.first()
        return preferences[SHOW_TUTORIAL_KEY] ?: true
    }

    suspend fun setTutorialShown() {
        dataStoreManager.dataStore.edit { preferences ->
            preferences[SHOW_TUTORIAL_KEY] = false
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_FIRST_NAME_KEY = stringPreferencesKey("user_first_name")
        private val USER_LAST_NAME_KEY = stringPreferencesKey("user_last_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
        private val USER_BIRTH_DATE_KEY = stringPreferencesKey("user_birth_date")
        private val USER_GENDER_KEY = stringPreferencesKey("user_gender")
        private val USER_ADDRESS_KEY = stringPreferencesKey("user_address")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_IS_ASSESSMENT_COMPLETED_KEY = booleanPreferencesKey("user_is_assessment_completed")
        private val IS_FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")
        private val SHOW_TUTORIAL_KEY = booleanPreferencesKey("show_tutorial")
    }
} 