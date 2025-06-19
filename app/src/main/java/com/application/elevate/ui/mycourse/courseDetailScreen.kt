package com.application.elevate.ui.mycourse

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.elevate.ui.component.QuizItemView
import com.application.elevate.ui.component.VideoItemView
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourseDetails
import com.application.elevate.model.CourseDetail
import com.application.elevate.model.CourseQuizItem
import com.application.elevate.model.CourseVideoItem

import com.application.elevate.model.QuizSubmitRequest
import com.application.elevate.viewmodel.course.CourseViewModel
import com.application.elevate.viewmodel.course.QuizCompletionData
import com.application.elevate.data.mapper.getLocalImageRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Int,
    onBackClick: () -> Unit,
    viewModel: CourseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Video") }

    LaunchedEffect(courseId) {
        viewModel.getCourseDetail(courseId)
        viewModel.getCourseVideos(courseId)
        viewModel.getCourseQuizzes(courseId)
    }

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
    
    // Offline Quiz Submit Dialog
    if (uiState.showOfflineQuizSubmitDialog && uiState.offlineQuizSubmitMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissOfflineQuizSubmitDialog() },
            title = { 
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF9800), // Orange color
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quiz Disimpan Offline",
                        color = Color(0xFFFF9800)
                    )
                }
            },
            text = {
                Text(
                    text = uiState.offlineQuizSubmitMessage!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissOfflineQuizSubmitDialog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }
    
    // Quiz Success Dialog
    if (uiState.showQuizSuccessDialog && uiState.quizSuccessMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissQuizSuccessDialog() },
            title = { 
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sukses!",
                        color = Color.Green
                    )
                }
            },
            text = {
                Text(
                    text = uiState.quizSuccessMessage!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissQuizSuccessDialog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Terjadi kesalahan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.getCourseDetail(courseId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            uiState.selectedCourse != null -> {
                val course = uiState.selectedCourse!!
                val courseItem = uiState.selectedCourseItem
                // PERBAIKAN: Gunakan status enrollment dari course.isEnrolled karena sudah di-update di Repository
                val isEnrolled = course.isEnrolled
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    // Hero Section with Lock Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        // Background Image
                        Image(
                            painter = painterResource(id = course.getLocalImageRes()),
                            contentDescription = course.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Lock Overlay when not enrolled
                        if (!isEnrolled) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Next Course Awaits!",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Unlock new knowledge by\ncompleting the previous milestone!",
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // Course Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = course.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            val totalVideos = if (uiState.courseVideos.isEmpty()) 0 else uiState.courseVideos.size
                            Text(
                                text = "${totalVideos.toString().padStart(2, '0')}/${totalVideos.toString().padStart(2, '0')}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                            
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Current video/lesson title
                        val currentVideoTitle = when {
                            selectedTab == "Video" && uiState.courseVideos.isNotEmpty() -> uiState.courseVideos.first().title
                            selectedTab == "Quiz" && uiState.courseQuizzes.isNotEmpty() -> uiState.courseQuizzes.first().question
                            selectedTab == "Video" -> "Belum ada video tersedia"
                            selectedTab == "Quiz" -> "Belum ada quiz tersedia"
                            else -> "Tidak ada konten"
                        }
                            
                            Text(
                            text = currentVideoTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = course.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                        // Action Buttons - Only show Buy Course button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!isEnrolled) {
                                // Show Buy Course button if not enrolled
                                if (uiState.isLoading) {
                                    Button(
                                        onClick = { },
                                        modifier = Modifier.weight(1f),
                                        enabled = false,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6C63FF)
                                        ),
                                        shape = RoundedCornerShape(25.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Processing...")
                                    }
                                } else {
                                    Button(
                                        onClick = { 
                                            viewModel.enrollCourse(courseId)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6C63FF)
                                        ),
                                        shape = RoundedCornerShape(25.dp)
                                    ) {
                                        Text("Buy Course")
                                    }
                                }
                            }
                            // No download button needed - course auto-saves after enrollment
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Tabs
                        TabRow(
                            selectedTabIndex = if (selectedTab == "Video") 0 else 1,
                            containerColor = Color.Transparent,
                            divider = {}
                        ) {
                            Tab(
                                text = { Text("Video") },
                                selected = selectedTab == "Video",
                                onClick = { selectedTab = "Video" },
                                selectedContentColor = Color(0xFF6C63FF),
                                unselectedContentColor = Color.Gray
                            )
                            Tab(
                                text = { Text("Quiz") },
                                selected = selectedTab == "Quiz",
                                onClick = { selectedTab = "Quiz" },
                                selectedContentColor = Color(0xFF6C63FF),
                                unselectedContentColor = Color.Gray
                            )
                        }
                    }
                    
                    // Content
                    when (selectedTab) {
                        "Video" -> {
                            VideoTabContent(
                                videos = uiState.courseVideos,
                                isEnrolled = isEnrolled,
                                isLoading = uiState.isLoadingVideos,
                                isLoadingVideoProxy = uiState.isLoadingVideoProxy,
                                onVideoClick = { video ->
                                    viewModel.playVideo(video.id)
                                }
                            )
                        }
                        "Quiz" -> {
                            QuizTabContent(
                                quizzes = uiState.courseQuizzes,
                                isEnrolled = isEnrolled,
                                isLoading = uiState.isLoadingQuizzes,
                                isQuizCompleted = viewModel.isQuizCompletedForCourse(courseId),
                                quizCompletionData = viewModel.getQuizCompletionData(courseId),
                                isOffline = uiState.isOffline,
                                isQuizPendingSubmission = uiState.isQuizPendingSubmission,
                                onSubmitQuiz = { answers: List<String> ->
                                    val request = QuizSubmitRequest(answers)
                                    viewModel.submitAllQuizzes(courseId, request)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoTabContent(
    videos: List<CourseVideoItem>,
    isEnrolled: Boolean,
    isLoading: Boolean,
    isLoadingVideoProxy: Boolean,
    onVideoClick: (CourseVideoItem) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (videos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "No Videos",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum ada video tersedia",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Text(
                    text = "Video akan segera ditambahkan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(videos) { video ->
                VideoItemCard(
                    video = video,
                    isEnrolled = isEnrolled,
                    isLoadingVideoProxy = isLoadingVideoProxy,
                    onClick = { onVideoClick(video) }
                )
            }
        }
    }
}

@Composable
fun VideoItemCard(
    video: CourseVideoItem,
    isEnrolled: Boolean,
    isLoadingVideoProxy: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnrolled && !video.isLocked && !isLoadingVideoProxy) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isEnrolled || video.isLocked) Color.Gray.copy(alpha = 0.3f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFF6C63FF).copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!isEnrolled || video.isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (isLoadingVideoProxy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF6C63FF),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color(0xFF6C63FF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (!isEnrolled || video.isLocked) Color.Gray else Color.Black
                )
                Text(
                    text = "12:00", // Duration placeholder
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            if (!isEnrolled || video.isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun QuizTabContent(
    quizzes: List<CourseQuizItem>,
    isEnrolled: Boolean,
    isLoading: Boolean,
    isQuizCompleted: Boolean = false,
    quizCompletionData: QuizCompletionData? = null,
    isOffline: Boolean = false,
    isQuizPendingSubmission: Boolean = false,
    onSubmitQuiz: (List<String>) -> Unit
) {
    var selectedAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (quizzes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "No Quizzes",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum ada quiz tersedia",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Text(
                    text = "Quiz akan segera ditambahkan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    } else if (isQuizPendingSubmission && isOffline) {
        // Show offline pending submission state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFFFF9800)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Anda Sedang Offline",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFFF9800)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Sambungkan ke internet untuk mengumpulkan jawaban quiz",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📝",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                text = "Jawaban Tersimpan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                            Text(
                                text = "Menunggu koneksi",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    } else if (isQuizCompleted && quizCompletionData != null) {
        // Show quiz completion status
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
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
                    modifier = Modifier.padding(32.dp),
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
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Quiz Sudah Selesai!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Anda telah menyelesaikan quiz ini",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${quizCompletionData.score}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "dari ${quizCompletionData.totalQuestions}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Score",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (quizCompletionData.isPassed) 
                                    Color.Green.copy(alpha = 0.2f) 
                                else 
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (quizCompletionData.isPassed) "✅" else "❌",
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Text(
                                    text = if (quizCompletionData.isPassed) "Lulus" else "Gagal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (quizCompletionData.isPassed) 
                                        Color.Green 
                                    else 
                                        MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Status",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Selesai pada ${java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("id", "ID")).format(java.util.Date(quizCompletionData.completedAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quizzes) { quiz ->
                    QuizItemCard(
                        quiz = quiz,
                        isEnrolled = isEnrolled,
                        selectedAnswer = selectedAnswers[quiz.id],
                        onAnswerSelected = { answerIndex ->
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(quiz.id, answerIndex)
                            }
                        }
                    )
                }
            }
            
            // Submit button for all quizzes
            if (isEnrolled && quizzes.isNotEmpty() && quizzes.none { it.isLocked }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Progress: ${selectedAnswers.size}/${quizzes.size} pertanyaan dijawab",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = {
                                val answers = quizzes.map { quiz ->
                                    selectedAnswers[quiz.id]?.toString() ?: "0"

                                }

                                onSubmitQuiz(answers)
                                Log.d("QuizTabContent", " Answer: ${answers.joinToString(", ")}")

                            },
                            enabled = selectedAnswers.size == quizzes.size,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C63FF)
                            )
                        ) {
                            Text(
                                text = if (selectedAnswers.size == quizzes.size) 
                                    "Submit All Answers" 
                                else 
                                    "Jawab semua pertanyaan (${selectedAnswers.size}/${quizzes.size})"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizItemCard(
    quiz: CourseQuizItem,
    isEnrolled: Boolean,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isEnrolled || quiz.isLocked) Color.Gray.copy(alpha = 0.3f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Quiz question
            Text(
                text = quiz.question,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (!isEnrolled || quiz.isLocked) Color.Gray else Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Answer choices with radio buttons
            quiz.options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isEnrolled && !quiz.isLocked) {
                            onAnswerSelected(index)
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == index,
                        onClick = {
                            if (isEnrolled && !quiz.isLocked) {
                                onAnswerSelected(index)
                            }
                        },
                        enabled = isEnrolled && !quiz.isLocked,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF6C63FF)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (!isEnrolled || quiz.isLocked) Color.Gray else Color.Black
                    )
                }
            }
            
            if (!isEnrolled || quiz.isLocked) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock course to access quiz",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDetailScreenPreview() {
    CourseDetailScreen(
        courseId = 1,
        onBackClick = {}
    )
} 