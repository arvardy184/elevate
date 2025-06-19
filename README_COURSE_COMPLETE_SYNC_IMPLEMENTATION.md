# Course Complete Sync Implementation - Solusi Lengkap

## 🎯 **Masalah yang Diperbaiki**

### 1. **Quiz Offline Tidak Auto-Sync**
- **Masalah**: Quiz yang disubmit saat offline tidak otomatis dikirim saat online
- **Solusi**: Implementasi `CourseSyncWorker` dengan auto-sync seperti ProfileSyncWorker

### 2. **Course Tidak Menggunakan Worker**
- **Masalah**: Course tidak menggunakan background worker untuk sync
- **Solusi**: Implementasi `CourseSyncWorker` terintegrasi dengan `MainActivity`

### 3. **Download Button Tidak Hilang**
- **Masalah**: Tombol download masih muncul meskipun course sudah di-download
- **Solusi**: Logic kondisional dengan 3 status (Not Enrolled, Enrolled + Not Downloaded, Downloaded)

### 4. **Buy Course Tidak Langsung Sync**
- **Masalah**: Saat buy course online layar tidak langsung update, harus diklik 2x
- **Solusi**: Auto-trigger sync worker dan update UI state immediately

---

## 🚀 **Implementasi Lengkap**

### A. **CourseSyncWorker Implementation**

#### 1. **Worker Class dengan Auto-Sync**
```kotlin
@HiltWorker
class CourseSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (!networkMonitor.isCurrentlyOnline()) {
                Log.d(TAG, "Device is offline, retrying sync later")
                return Result.retry()
            }

            val token = userRepository.getAuthToken()
            if (token == null) {
                Log.e(TAG, "No auth token available for sync")
                return Result.failure()
            }

            Log.d(TAG, "Starting course data sync...")
            
            // Sync pending quiz answers and enrollments
            val syncResult = courseRepository.syncPendingData(token)
            
            syncResult.fold(
                onSuccess = { success ->
                    if (success) {
                        Log.d(TAG, "Course sync completed successfully")
                        Result.success()
                    } else {
                        Log.w(TAG, "Course sync partially failed")
                        Result.retry()
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Course sync failed", error)
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Course sync worker error", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "CourseSyncWorker"
        private const val WORK_NAME = "course_sync_work"

        fun enqueue(context: Context) {
            val workManager = WorkManager.getInstance(context)
            
            // One-time immediate sync
            val immediateRequest = OneTimeWorkRequestBuilder<CourseSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            
            workManager.enqueueUniqueWork(
                "${WORK_NAME}_immediate",
                ExistingWorkPolicy.REPLACE,
                immediateRequest
            )
            
            // Periodic sync every 1 hour
            val periodicRequest = PeriodicWorkRequestBuilder<CourseSyncWorker>(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
            
            Log.d(TAG, "Course sync worker enqueued")
        }
    }
}
```

#### 2. **Integrasi di MainActivity**
```kotlin
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var syncManager: SyncManager
    
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private fun initializeSync() {
        // Schedule existing sync
        syncManager.schedulePeriodicSync()
        syncManager.triggerImmediateSync()
        
        // Schedule course sync worker
        CourseSyncWorker.enqueue(this)
        
        // Monitor network changes and trigger sync when online
        lifecycleScope.launch {
            networkMonitor.isOnline().collect { isOnline ->
                if (isOnline) {
                    // Trigger immediate sync when device goes online
                    syncManager.triggerImmediateSync()
                    CourseSyncWorker.enqueue(this@MainActivity)
                }
            }
        }
    }
}
```

### B. **Auto-Sync CourseViewModel Updates**

#### 1. **Network Status Monitoring dengan Auto-Sync**
```kotlin
private fun observeNetworkStatus() {
    viewModelScope.launch {
        networkMonitor.isOnline().collect { isOnline ->
            _uiState.update { 
                it.copy(isOffline = !isOnline) 
            }
            
            // If online, trigger automatic sync
            if (isOnline) {
                Log.d(TAG, "Device is now online - triggering automatic sync")
                
                // Trigger immediate sync worker for quiz answers and enrollments
                CourseSyncWorker.enqueue(context)
                
                // Auto-sync pending data
                syncPendingData()
                
                checkPendingSync()
            }
        }
    }
}
```

#### 2. **Enhanced enrollCourse dengan Worker Trigger**
```kotlin
fun enrollCourse(courseId: Int) {
    viewModelScope.launch {
        try {
            val token = userRepository.getAuthToken()
            if (token == null) {
                _uiState.update { 
                    it.copy(error = "Sesi anda telah berakhir. Silakan login kembali.") 
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            
            val result = courseRepository.enrollCourse(token, courseId)
            result.onSuccess { response ->
                Log.d(TAG, "Course enrolled successfully")
                
                // Trigger immediate sync worker to ensure all data is fresh
                CourseSyncWorker.enqueue(context)
                
                // Update selected course detail to reflect enrollment
                val updatedCourse = response.course
                
                // Fetch ulang courses untuk mendapatkan status isEnrolled yang updated
                fetchCourses()
                getCourseDetail(courseId) // Re-fetch course detail untuk UI yang fresh
                
                // Fetch ulang video dan quiz setelah enroll
                getCourseVideos(courseId)
                getCourseQuizzes(courseId)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        selectedCourse = updatedCourse, // Update immediately
                        error = null
                    ) 
                }
            }.onFailure { error ->
                // Error handling...
            }
        } catch (e: Exception) {
            // Exception handling...
        }
    }
}
```

### C. **Smart Download Button Implementation**

#### 1. **Enhanced UI State Check**
```kotlin
// In getCourseDetail method
val isAvailableOffline = courseRepository.isCourseAvailableOffline(courseId)

_uiState.update { 
    it.copy(
        isLoading = false,
        selectedCourse = response.course,
        selectedCourseItem = selectedCourseItem,
        isCourseAvailableOffline = isAvailableOffline,
        error = null
    ) 
}
```

#### 2. **Three-State Button Logic in courseDetailScreen.kt**
```kotlin
// Action Buttons
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    when {
        !isEnrolled -> {
            // Show Buy Course button if not enrolled
            if (uiState.isLoading) {
                Button(/* Processing state */) {
                    CircularProgressIndicator()
                    Text("Processing...")
                }
            } else {
                Button(onClick = { viewModel.enrollCourse(courseId) }) {
                    Text("Buy Course")
                }
            }
        }
        isEnrolled && !uiState.isCourseAvailableOffline -> {
            // Show Download button if enrolled but not downloaded
            if (uiState.isDownloadingCourse) {
                Button(/* Downloading state */) {
                    CircularProgressIndicator()
                    Text("Downloading...")
                }
            } else {
                Button(onClick = { viewModel.downloadCourseForOffline(courseId) }) {
                    Icon(Icons.Default.Download)
                    Text("Download")
                }
            }
        }
        isEnrolled && uiState.isCourseAvailableOffline -> {
            // Show Downloaded indicator if course is already downloaded
            Button(
                enabled = false,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.CheckCircle)
                Text("Downloaded")
            }
        }
    }
}
```

### D. **Improved isEnroll Status Handling**

#### 1. **Enhanced CourseRepository Logic**
```kotlin
suspend fun getCourseDetail(courseId: Int): Result<CourseDetailResponse> {
    return try {
        val isOnline = networkMonitor.isCurrentlyOnline()
        
        if (isOnline) {
            Log.d(TAG, "Online - Mengambil detail course dari API")
            val response = courseApiService.getCourseDetail(courseId)
            
            // Save to local database
            val courseDetailEntity = response.course.toEntity()
            courseOfflineRepository.saveCourseDetail(courseDetailEntity)
            
            Result.success(response)
        } else {
            Log.d(TAG, "Offline - Mengambil detail course dari local database")
            val localCourseDetail = courseOfflineRepository.getCourseDetailById(courseId)
            val localCourse = courseOfflineRepository.getCourseById(courseId)
            
            if (localCourseDetail != null) {
                val apiCourseDetail = localCourseDetail.toApiModel()
                
                // PERBAIKAN: Set status enrollment dari local course data
                if (localCourse != null) {
                    apiCourseDetail.isEnrolled = localCourse.isEnrolled
                }
                
                val response = CourseDetailResponse(course = apiCourseDetail)
                Result.success(response)
            } else {
                Result.failure(Exception("Detail course tidak tersedia offline."))
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting course detail", e)
        Result.failure(e)
    }
}
```

---

## 📋 **Key Features Implemented**

### ✅ **1. Auto-Sync Quiz Offline**
- Quiz answers tersimpan local saat offline
- Auto-sync saat device online kembali
- Background worker dengan retry mechanism
- Network status monitoring

### ✅ **2. Worker Integration**
- `CourseSyncWorker` untuk course data
- Periodic sync setiap 1 jam
- Immediate sync saat network available
- Integration dengan `MainActivity`

### ✅ **3. Smart Download Button**
- **Not Enrolled**: Show "Buy Course"
- **Enrolled + Not Downloaded**: Show "Download"
- **Enrolled + Downloaded**: Show "Downloaded" (disabled, green)
- Loading states untuk semua kondisi

### ✅ **4. Instant Buy Course Sync**
- Auto-trigger worker saat enrollment
- Immediate UI update
- Re-fetch all related data
- Status enrollment tersinkron

### ✅ **5. Enhanced Offline Support**
- IsEnroll status dibaca dari local database
- Course availability detection
- Fallback ke local data saat offline
- Network monitoring dengan auto-sync

---

## 🔧 **Technical Implementation Details**

### **1. Worker Architecture**
- `@HiltWorker` untuk dependency injection
- Network constraint untuk sync
- ExistingWorkPolicy untuk avoid duplicates
- Retry mechanism untuk failed sync

### **2. Repository Pattern**
- Separation of concerns
- Online/offline data flow
- Local cache dengan Room database
- Network monitoring integration

### **3. UI State Management**
- Reactive UI dengan StateFlow
- Loading states untuk better UX
- Error handling dan retry logic
- Auto-update saat data berubah

### **4. Error Handling**
- Network error handling
- Authentication error handling
- Local data fallback
- User-friendly error messages

---

## 🎯 **Hasil Implementasi**

### **Before:**
❌ Quiz offline tidak tersync  
❌ Course tidak pakai worker  
❌ Download button selalu muncul  
❌ Buy course perlu klik 2x  

### **After:**
✅ Quiz auto-sync saat online  
✅ Course menggunakan background worker  
✅ Download button smart (3 states)  
✅ Buy course langsung sync + update UI  

---

## 🔮 **Future Enhancements**

1. **Progressive Sync**: Sync data secara bertahap
2. **Conflict Resolution**: Handle data conflicts
3. **Sync Progress**: Show sync progress to user
4. **Selective Sync**: Allow user to choose what to sync
5. **Background Upload**: Upload large files in background

---

*Implementasi ini memberikan experience yang seamless untuk user dengan auto-sync yang reliable dan UI yang responsive.* 