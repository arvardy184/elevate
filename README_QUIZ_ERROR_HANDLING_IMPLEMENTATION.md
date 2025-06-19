# Quiz Error Handling & Completion State Implementation

## Ringkasan Implementasi

Implementasi ini memperbaiki error handling untuk quiz submission dengan:
1. **Parsing response error yang lebih jelas** untuk HTTP 400 dan error lainnya
2. **Popup error dialog** yang informatif
3. **State management quiz completion** untuk menampilkan status "quiz sudah selesai"
4. **UI quiz completion** yang menggantikan form quiz setelah selesai

## 🔧 Perubahan yang Dibuat

### 1. Model Layer - ApiErrorResponse

**File**: `app/src/main/java/com/application/elevate/model/CourseQuiz.kt`

Menambahkan model untuk parsing error response API:

```kotlin
// Model untuk error response API
data class ApiErrorResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("errors")
    val errors: Map<String, List<String>>?
)
```

### 2. ViewModel Layer - Enhanced Error Handling

**File**: `app/src/main/java/com/application/elevate/viewmodel/course/CourseViewModel.kt`

#### **A. Enhanced CourseUiState**
```kotlin
data class CourseUiState(
    // ... existing fields ...
    // Quiz error and completion states
    val showQuizError: Boolean = false,
    val quizErrorMessage: String? = null,
    val completedQuizCourseIds: Set<Int> = emptySet(),
    val quizCompletionData: Map<Int, QuizCompletionData> = emptyMap()
)

data class QuizCompletionData(
    val score: Int,
    val totalQuestions: Int,
    val isPassed: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)
```

#### **B. Enhanced submitAllQuizzes Method**
```kotlin
fun submitAllQuizzes(courseId: Int, request: QuizSubmitRequest) {
    viewModelScope.launch {
        // ... token validation ...
        
        val result = courseRepository.submitAllQuizzes(token, courseId, request)
        result.onSuccess { response ->
            // Mark quiz as completed for this course
            val completionData = QuizCompletionData(
                score = response.score,
                totalQuestions = response.totalQuestions,
                isPassed = response.isPassed
            )
            
            _uiState.update { currentState ->
                currentState.copy(
                    // ... existing updates ...
                    completedQuizCourseIds = currentState.completedQuizCourseIds + courseId,
                    quizCompletionData = currentState.quizCompletionData + (courseId to completionData)
                ) 
            }
        }.onFailure { error ->
            // Parse error response for better message
            val errorMessage = parseQuizErrorMessage(error)
            
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    showQuizError = true,
                    quizErrorMessage = errorMessage
                ) 
            }
        }
    }
}
```

#### **C. Enhanced Error Parsing**
```kotlin
private fun parseQuizErrorMessage(error: Throwable): String {
    return when {
        error is retrofit2.HttpException -> {
            when (error.code()) {
                400 -> {
                    try {
                        val errorBody = error.response()?.errorBody()?.string()
                        if (!errorBody.isNullOrEmpty()) {
                            val gson = com.google.gson.Gson()
                            val apiError = gson.fromJson(errorBody, ApiErrorResponse::class.java)
                            apiError?.message ?: "Quiz sudah pernah dikerjakan atau ada masalah dengan jawaban"
                        } else {
                            "Quiz sudah pernah dikerjakan atau ada masalah dengan jawaban yang dikirim"
                        }
                    } catch (e: Exception) {
                        "Quiz sudah pernah dikerjakan atau ada masalah dengan jawaban yang dikirim"
                    }
                }
                401 -> "Sesi anda telah berakhir. Silakan login kembali"
                403 -> "Anda tidak memiliki akses untuk mengerjakan quiz ini"
                404 -> "Quiz tidak ditemukan"
                409 -> "Quiz sudah pernah dikerjakan sebelumnya"
                422 -> "Data quiz yang dikirim tidak valid. Pastikan semua pertanyaan terjawab"
                500 -> "Server sedang bermasalah. Coba lagi dalam beberapa menit"
                else -> "Terjadi kesalahan pada server (${error.code()})"
            }
        }
        error.message?.contains("timeout", ignoreCase = true) == true -> 
            "Koneksi timeout. Periksa koneksi internet dan coba lagi"
        error.message?.contains("network", ignoreCase = true) == true -> 
            "Masalah koneksi internet. Periksa koneksi dan coba lagi"
        else -> error.message ?: "Gagal mengirim jawaban quiz"
    }
}
```

#### **D. New Helper Methods**
```kotlin
fun dismissQuizError() {
    _uiState.update { it.copy(showQuizError = false, quizErrorMessage = null) }
}

fun isQuizCompletedForCourse(courseId: Int): Boolean {
    return _uiState.value.completedQuizCourseIds.contains(courseId)
}

fun getQuizCompletionData(courseId: Int): QuizCompletionData? {
    return _uiState.value.quizCompletionData[courseId]
}
```

### 3. UI Layer - Enhanced Dialogs & Quiz Completion

**File**: `app/src/main/java/com/application/elevate/ui/mycourse/courseDetailScreen.kt`

#### **A. Enhanced Quiz Result Dialog**
```kotlin
// Quiz Result Dialog
if (uiState.showQuizResult && uiState.quizSubmitResponse != null) {
    AlertDialog(
        onDismissRequest = { viewModel.dismissQuizResult() },
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (uiState.quizSubmitResponse!!.isPassed) 
                        Icons.Default.CheckCircle 
                    else 
                        Icons.Default.Error,
                    contentDescription = null,
                    tint = if (uiState.quizSubmitResponse!!.isPassed) 
                        Color.Green 
                    else 
                        MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hasil Quiz")
            }
        },
        text = {
            // Enhanced result display with Card
            Column {
                Text(
                    text = uiState.quizSubmitResponse!!.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.quizSubmitResponse!!.isPassed) 
                            Color.Green.copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Score: ${uiState.quizSubmitResponse!!.score}/${uiState.quizSubmitResponse!!.totalQuestions}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Status: ${if (uiState.quizSubmitResponse!!.isPassed) "✅ Lulus" else "❌ Tidak Lulus"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.quizSubmitResponse!!.isPassed) 
                                Color.Green 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.dismissQuizResult() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("OK")
            }
        }
    )
}
```

#### **B. New Quiz Error Dialog**
```kotlin
// Quiz Error Dialog
if (uiState.showQuizError && uiState.quizErrorMessage != null) {
    AlertDialog(
        onDismissRequest = { viewModel.dismissQuizError() },
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Error Quiz",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        text = {
            Text(
                text = uiState.quizErrorMessage!!,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = { viewModel.dismissQuizError() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("OK")
            }
        }
    )
}
```

#### **C. Enhanced QuizTabContent**
Updated dengan parameter baru:
```kotlin
@Composable
fun QuizTabContent(
    quizzes: List<CourseQuizItem>,
    isEnrolled: Boolean,
    isLoading: Boolean,
    isQuizCompleted: Boolean = false,
    quizCompletionData: QuizCompletionData? = null,
    onSubmitQuiz: (List<QuizAnswer>) -> Unit
) {
    // ... existing loading/empty states ...
    
    } else if (isQuizCompleted && quizCompletionData != null) {
        // Show quiz completion status
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (quizCompletionData.isPassed) 
                        Color.Green.copy(alpha = 0.1f) 
                    else 
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (quizCompletionData.isPassed) 
                            Icons.Default.CheckCircle 
                        else 
                            Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = if (quizCompletionData.isPassed) 
                            Color.Green 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Quiz Sudah Selesai!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    // Score and Status Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Card(/*Score Card*/) { /*...*/ }
                        Card(/*Status Card*/) { /*...*/ }
                    }
                    
                    // Completion timestamp
                    Text(
                        text = "Selesai pada ${SimpleDateFormat(...).format(Date(...))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        // Regular quiz form (existing implementation)
    }
}
```

## 🎯 Fitur yang Diimplementasi

### ✅ Enhanced Error Handling
- **Parsing HTTP 400**: Response body diparsing untuk pesan error yang lebih spesifik
- **Comprehensive Error Codes**: Menangani 400, 401, 403, 404, 409, 422, 500
- **Network Error Handling**: Timeout dan network errors dengan pesan yang jelas
- **Fallback Messages**: Default messages untuk unknown errors

### ✅ Error Popup Dialog
- **Styled Error Display**: Icon error dengan warna yang sesuai  
- **Clear Error Messages**: Pesan error yang mudah dipahami user
- **Dismissible**: User dapat menutup dialog dengan tombol OK

### ✅ Quiz Completion State
- **Persistent State**: Quiz completion disimpan di ViewModel state
- **Completion Data**: Score, status, dan timestamp completion tersimpan
- **Course-specific**: Each course memiliki completion state terpisah

### ✅ Enhanced Quiz Completion UI
- **Visual Status**: Icon checkmark/error dengan color coding
- **Score Display**: Score cards dengan styling yang menarik
- **Status Badge**: Pass/Fail status dengan emoji dan warna
- **Timestamp**: Waktu completion dengan format Indonesia
- **Complete Replacement**: Quiz form digantikan sepenuhnya dengan completion view

### ✅ Improved Success Dialog
- **Enhanced Result Dialog**: Styling yang lebih menarik dengan cards
- **Visual Indicators**: Icons untuk pass/fail status
- **Better Typography**: Hierarchy yang jelas dengan colors

## 🚀 User Experience Improvements

1. **Clear Error Communication**: User tidak lagi melihat "Bad Request" tapi pesan yang jelas seperti "Quiz sudah pernah dikerjakan"

2. **Popup Notifications**: Error langsung muncul sebagai popup, tidak perlu scroll atau cari error message

3. **Quiz Completion Persistence**: Setelah quiz selesai, user melihat status completion, bukan form kosong

4. **Visual Feedback**: Icons, colors, dan styling yang membantu user memahami status dengan cepat

5. **Comprehensive Error Coverage**: Semua kemungkinan error code ditangani dengan pesan yang sesuai

## 🔍 Testing Scenarios

1. **HTTP 400 Error**: Submit quiz yang sudah pernah dikerjakan
2. **Network Timeout**: Test dengan koneksi lambat
3. **Session Expired**: Test dengan token expired
4. **Quiz Completion**: Submit quiz berhasil lalu cek tampilan completion
5. **Error Dialog**: Verifikasi popup muncul dan dapat ditutup
6. **State Persistence**: Quiz completion state bertahan saat navigate

## 📱 Screenshot Mockup

```
┌─────────────────────────┐
│ ❌ Error Quiz           │
├─────────────────────────┤
│ Quiz sudah pernah       │
│ dikerjakan atau ada     │
│ masalah dengan jawaban  │
│ yang dikirim           │
├─────────────────────────┤
│              [OK]       │
└─────────────────────────┘

┌─────────────────────────┐
│     Quiz Sudah Selesai! │
│                        │
│       ✅ (80px icon)    │
│                        │
│ Anda telah menyelesaikan│
│        quiz ini        │
│                        │
│ ┌─────┐  ┌─────────┐   │
│ │  8  │  │   ✅    │   │
│ │dari │  │  Lulus  │   │
│ │ 10  │  │ Status  │   │
│ │Score│  │         │   │
│ └─────┘  └─────────┘   │
│                        │
│ Selesai pada           │
│ 18 Juni 2025, 22:18    │
└─────────────────────────┘
```

Implementasi ini memberikan pengalaman user yang jauh lebih baik dengan error handling yang jelas dan UI completion yang informatif! 🎉 