package com.application.elevate.ui.mycourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.core.app.NotificationCompat.Style
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R
import com.application.elevate.component.QuizItemView
import com.application.elevate.component.VideoItemView
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourseDetails
import com.application.elevate.model.CourseDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(courseDetail: CourseDetail, onBackClick: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Video") }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = courseDetail.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        Box(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ) {
                // Gambar Header
                Image(
                    painter = painterResource(id = courseDetail.imageRes),
                    contentDescription = "Course Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

                // Info Course
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = courseDetail.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = courseDetail.subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = courseDetail.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { /* Download action */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Download Course", 
                            color = Color.White, 
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Tab Video & Quiz
                TabRow(
                    selectedTabIndex = if (selectedTab == "Video") 0 else 1,
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[if (selectedTab == "Video") 0 else 1])
                                .height(3.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    },
                    divider = { }
                ) {
                    Tab(
                        selected = selectedTab == "Video",
                        onClick = { selectedTab = "Video" },
                        text = { Text("Video", fontWeight = FontWeight.Medium) }
                    )
                    Tab(
                        selected = selectedTab == "Quiz",
                        onClick = { selectedTab = "Quiz" },
                        text = { Text("Quiz", fontWeight = FontWeight.Medium) }
                    )
                }

                // Content List
                Column(modifier = Modifier.padding(16.dp)) {
                    if (selectedTab == "Video") {
                        courseDetail.videoList.forEach {
                            VideoItemView(it)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        courseDetail.quizList.forEach {
                            QuizItemView(it)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    // Bottom spacer for better UX
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDetailScreenPreview() {
    val courseDetail = dummyCourseDetails.first()
    CourseDetailScreen(courseDetail = courseDetail, onBackClick = {})
} 