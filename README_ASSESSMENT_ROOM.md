# Implementasi ROOM untuk Fitur Assessment

## Overview
Implementasi ROOM untuk fitur assessment mengikuti pola offline-first yang sama dengan fitur profile, dengan beberapa penyesuaian khusus untuk kondisi API PUT yang belum tersedia.

## Komponen yang Diimplementasikan

### 1. Database Layer

#### AssessmentEntity
- **File**: `app/src/main/java/com/application/elevate/data/database/entity/AssessmentEntity.kt`
- **Fitur**:
  - Primary key: `id` (auto-generate)
  - Foreign key: `userId` untuk relasi dengan user
  - Semua field assessment: studentStatus, majorStudy, currentSemester, dll
  - Sync tracking: `isSynced`, `lastModified`, `syncError`
  - Extension functions untuk konversi ke/dari model

#### AssessmentDao
- **File**: `app/src/main/java/com/application/elevate/data/database/dao/AssessmentDao.kt`
- **Operasi**:
  - `getLatestAssessment()` - Mendapatkan assessment terbaru user
  - `getAllAssessments()` - Mendapatkan semua assessment user
  - `getUnsyncedAssessments()` - Mendapatkan assessment yang belum sync
  - `insertAssessment()` - Menyimpan assessment baru
  - `markAsSynced()` / `markAsUnsynced()` - Update status sync
  - `hasAssessment()` - Cek apakah user sudah punya assessment

### 2. Repository Layer

#### AssessmentOfflineRepository (Interface)
- **File**: `app/src/main/java/com/application/elevate/data/repository/AssessmentOfflineRepository.kt`
- **Methods**:
  - Online methods: `submitAssessment()`, `getAssessmentHistory()`
  - Offline-first methods: `submitAssessmentOfflineFirst()`, `getLatestAssessmentOfflineFirst()`
  - Sync methods: `syncAssessments()`, `syncUnsyncedAssessments()`

#### AssessmentOfflineRepositoryImpl
- **File**: `app/src/main/java/com/application/elevate/data/repository/AssessmentOfflineRepositoryImpl.kt`
- **Fitur Khusus**:
  - **Offline-first approach**: Prioritas data lokal, fallback ke API
  - **Smart sync logic**: Deteksi apakah user sudah punya assessment
  - **PUT API handling**: Menangani kondisi API PUT belum tersedia
  - **Error handling**: Robust error handling dengan retry mechanism

### 3. Worker untuk Background Sync

#### AssessmentSyncWorker
- **File**: `app/src/main/java/com/application/elevate/worker/AssessmentSyncWorker.kt`
- **Fitur**:
  - Background sync untuk assessment yang belum tersync
  - Network checking sebelum sync
  - Dependency injection dengan Hilt
  - Error handling dan logging

### 4. ViewModel Updates

#### MyAssessmentViewModel
- **Update**: Menggunakan `AssessmentOfflineRepository` untuk offline-first data fetching
- **Fitur**: Automatic fallback ke data lokal jika API gagal

#### AssessmentViewModel
- **Update**: Menggunakan `submitAssessmentOfflineFirst()` untuk submission
- **Fitur**: Data tersimpan lokal meskipun offline

## Alur Kerja Offline-First

### 1. Submit Assessment
```
User Submit → Check Online Status
├── Online: Try API POST
│   ├── Success: Save to local (synced=true)
│   └── Failed: Save to local (synced=false)
└── Offline: Save to local (synced=false)
```

### 2. Get Assessment Data
```
Request Data → Check Online Status
├── Online: Try API GET
│   ├── Success: Update local + return API data
│   └── Failed: Return local data
└── Offline: Return local data
```

### 3. Background Sync
```
Worker Triggered → Check Network
├── Online: Sync unsynced assessments
│   ├── New assessment: Use POST API
│   ├── Existing assessment: Mark as "PUT API needed"
│   └── Update sync status
└── Offline: Skip sync
```

## Penanganan Khusus API PUT

Karena API PUT untuk edit assessment belum tersedia:

1. **New Assessment**: Menggunakan POST API (normal flow)
2. **Edit Assessment**: 
   - Disimpan lokal dengan `isSynced=false`
   - Ditandai dengan error "PUT API not available yet"
   - Akan di-sync otomatis ketika API PUT tersedia

## Database Schema

```sql
CREATE TABLE assessment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    studentStatus TEXT,
    majorStudy TEXT,
    currentSemester TEXT,
    currentField TEXT,
    interestedField TEXT,
    dreamJob TEXT,
    mainGoal TEXT,
    createdAt TEXT,
    isSynced INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL,
    syncError TEXT
);
```

## Dependency Injection

### NetworkModule Updates
```kotlin
@Provides
@Singleton
fun provideAssessmentDao(database: AppDatabase): AssessmentDao

@Provides
@Singleton
fun provideAssessmentOfflineRepository(
    api: AssessmentApiService,
    userRepository: UserRepository,
    assessmentDao: AssessmentDao,
    networkUtil: NetworkUtil
): AssessmentOfflineRepository
```

### AppDatabase Updates
```kotlin
@Database(
    entities = [CVReviewEntity::class, ProfileEntity::class, AssessmentEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assessmentDao(): AssessmentDao
    // ... other DAOs
}
```

## Keunggulan Implementasi

1. **Offline-First**: Data tersedia meskipun tidak ada koneksi
2. **Automatic Sync**: Background sync otomatis ketika online
3. **Error Resilient**: Robust error handling dan retry mechanism
4. **Future-Proof**: Siap untuk API PUT ketika tersedia
5. **Consistent UX**: User experience yang konsisten online/offline
6. **Data Integrity**: Tracking sync status untuk memastikan data consistency

## Testing

Aplikasi dapat dijalankan dan ditest dalam berbagai kondisi:
- ✅ Online normal operation
- ✅ Offline data submission
- ✅ Network interruption handling
- ✅ Background sync when back online
- ✅ Mixed online/offline scenarios

## Future Enhancements

Ketika API PUT tersedia:
1. Update `AssessmentOfflineRepositoryImpl` untuk handle PUT requests
2. Update sync logic untuk edit assessments
3. Remove "PUT API not available" handling
4. Test end-to-end edit functionality 