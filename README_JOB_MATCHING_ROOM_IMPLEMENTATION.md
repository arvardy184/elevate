# Implementasi ROOM untuk Fitur Job & Skill Matching

## Overview
Implementasi ROOM database untuk fitur Job & Skill Matching dengan dukungan offline/online mode, sinkronisasi data, dan user experience yang optimal.

## Fitur Utama
- ✅ **Offline Mode**: Simpan data job matching saat tidak ada koneksi internet
- ✅ **Auto Sync**: Sinkronisasi otomatis data ke server saat kembali online
- ✅ **Manual Sync**: Tombol sync manual untuk kontrol penuh oleh user
- ✅ **Pending Status**: Indikator visual untuk data yang belum tersinkronisasi
- ✅ **Error Handling**: Penanganan error komprehensif untuk berbagai skenario
- ✅ **Offline Dialog**: Notifikasi user saat bekerja dalam mode offline

## Struktur Database

### 1. Entity Classes
#### JobMatchingEntity
```kotlin
@Entity(tableName = "job_matching")
data class JobMatchingEntity(
    @PrimaryKey val id: String,
    val dreamJob: String,
    val matches: List<JobMatch>,
    val aiAnalysis: AIJobAnalysis,
    val createdAt: String,
    val totalMatches: Int,
    val cvSaved: Boolean = false,
    val isSynced: Boolean = false, // Field untuk tracking sinkronisasi
    val cvFilePath: String? = null, // Path file CV lokal
    val syncError: String? = null, // Error message saat sync gagal
    val isOfflineData: Boolean = false // Flag untuk data yang dibuat offline
)
```

#### JobMatchEntity & AIAnalysisEntity
- Detail matches dan analisis AI disimpan dalam tabel terpisah untuk normalisasi

### 2. DAO (Data Access Object)
```kotlin
interface JobMatchingDao {
    // CRUD operations
    fun getAllJobMatching(): Flow<List<JobMatchingEntity>>
    suspend fun insertJobMatching(jobMatching: JobMatchingEntity)
    suspend fun updateJobMatching(jobMatching: JobMatchingEntity)
    
    // Sync operations
    suspend fun getUnsyncedJobMatching(): List<JobMatchingEntity>
    suspend fun markAsSynced(id: String)
    suspend fun updateSyncError(id: String, error: String)
    
    // Transaction operations
    suspend fun insertCompleteJobMatching(...)
    suspend fun deleteCompleteJobMatching(jobMatchingId: String)
}
```

## Arsitektur Aplikasi

### 1. Repository Pattern
#### Online Repository (`JobMatchingRepositoryImpl`)
- Menghandle API calls dan network operations
- Menyimpan response API ke local database
- Mengecek status network sebelum API call

#### Offline Repository (`JobMatchingOfflineRepositoryImpl`)
- Menghandle local database operations
- Menyimpan data offline dengan dummy response
- Menghandle sync operations ke server

### 2. ViewModel (`JobMatchingViewModel`)
```kotlin
data class JobMatchingUiState(
    val isLoading: Boolean = false,
    val jobMatchingResult: JobMatchingResponse? = null,
    val historyResult: JobMatchingHistoryResponse? = null,
    val isOffline: Boolean = false,
    val unsyncedCount: Int = 0,
    val isSyncing: Boolean = false,
    val syncResults: List<SyncResult> = emptyList(),
    val showOfflineDialog: Boolean = false,
    val isOfflineUpload: Boolean = false
)
```

## Flow Implementasi

### 1. Online Flow
1. User upload CV dan pilih dream job
2. Cek koneksi internet (NetworkMonitor)
3. Jika online: kirim ke API
4. Simpan response ke local database dengan `isSynced = true`
5. Tampilkan hasil di UI

### 2. Offline Flow
1. User upload CV dan pilih dream job
2. Cek koneksi internet (NetworkMonitor)
3. Jika offline: simpan ke local database dengan:
   - `isSynced = false`
   - `isOfflineData = true`
   - `cvFilePath = path_file_cv`
   - Dummy response untuk UI
4. Tampilkan offline dialog
5. Tampilkan hasil dummy dengan status "PENDING"

### 3. Sync Flow
1. Saat kembali online, tampilkan sync status card
2. User dapat tekan tombol "Sync" atau auto sync
3. Ambil semua data dengan `isSynced = false`
4. Upload data offline ke API
5. Jika berhasil: hapus data offline, simpan response API dengan `isSynced = true`
6. Jika gagal: update `syncError` field
7. Update UI dengan hasil sync

## User Interface Components

### 1. SyncStatusCard
- Menampilkan status offline/online
- Menampilkan jumlah data yang belum sync
- Tombol sync dengan loading indicator

### 2. OfflineDialog
- Dialog informasi saat user bekerja offline
- Menjelaskan bahwa data akan disinkronkan nanti

### 3. PendingHistoryCard
- Card khusus untuk data yang belum sync
- Badge "PENDING" dengan warna orange
- Informasi bahwa data akan disinkronkan

## Error Handling

### 1. Network Errors
- Connection timeout
- No internet connection
- Server tidak dapat dijangkau

### 2. Sync Errors
- Authentication errors
- Server errors (5xx)
- File tidak ditemukan
- API validation errors

### 3. Database Errors
- Insert/Update failures
- Migration errors
- Storage space issues

## Testing Scenarios

### 1. Offline Mode Testing
- [ ] Upload CV saat offline
- [ ] Data tersimpan di local database
- [ ] Dialog offline muncul
- [ ] Status "PENDING" tampil di history

### 2. Online Mode Testing
- [ ] Upload CV saat online
- [ ] Data langsung terkirim ke API
- [ ] Response tersimpan di local database
- [ ] Status "SYNCED" di history

### 3. Sync Testing
- [ ] Tombol sync muncul saat ada data pending
- [ ] Sync berhasil: data terupdate, status berubah
- [ ] Sync gagal: error message tersimpan
- [ ] Loading indicator saat sync

### 4. Network Transition Testing
- [ ] Dari offline ke online: sync status muncul
- [ ] Dari online ke offline: tetap bisa akses data lokal
- [ ] Network intermittent: error handling yang tepat

## Performance Optimizations

### 1. Database
- Index pada kolom yang sering diquery
- Transaction batching untuk operasi multiple
- Lazy loading untuk data besar

### 2. Network
- Request caching
- Retry mechanism dengan exponential backoff
- Request deduplication

### 3. UI
- State hoisting untuk performance
- LazyColumn untuk list besar
- Image loading optimization

## Maintenance & Monitoring

### 1. Logging
- Network request/response logging
- Database operation logging
- Sync operation tracking
- Error reporting

### 2. Analytics
- Offline usage metrics
- Sync success/failure rates
- User interaction patterns
- Performance metrics

### 3. Database Migration
- Version management
- Migration strategy
- Rollback procedures
- Data validation

## Security Considerations

### 1. Data Storage
- CV file encryption saat disimpan lokal
- Sensitive data obfuscation
- Secure deletion of temporary files

### 2. Network
- Certificate pinning
- Request/response encryption
- Authentication token management

## Future Enhancements

### 1. Advanced Sync
- Conflict resolution untuk concurrent edits
- Incremental sync untuk efisiensi
- Background sync dengan WorkManager

### 2. Caching Strategy
- TTL (Time To Live) untuk cache data
- Cache invalidation strategy
- Preemptive caching

### 3. User Experience
- Progressive sync indicator
- Detailed sync history
- Customizable sync settings

---

## Cara Menjalankan

1. **Setup Database**: Database akan otomatis ter-create saat aplikasi pertama kali run
2. **Testing Offline**: Matikan wifi/data untuk test offline mode
3. **Testing Sync**: Nyalakan kembali koneksi dan tekan tombol sync
4. **Monitoring**: Cek logcat untuk detail operasi database dan network

## Dependencies Added
```kotlin
// Room
implementation "androidx.room:room-runtime:2.5.0"
implementation "androidx.room:room-ktx:2.5.0"
kapt "androidx.room:room-compiler:2.5.0"

// Network monitoring
implementation "androidx.core:core-ktx:1.12.0"

// Gson for type conversion
implementation "com.google.code.gson:gson:2.10.1"
``` 