    package com.application.elevate.ui.mycourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    navController: NavController = rememberNavController()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Video", "Quiz")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Empty title */ },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Course Banner Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ui_ux), // Replace with proper banner
                    contentDescription = "Course Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Course Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title and Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UI/UX Design",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "01/09",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                
                // Description
                Text(
                    text = "Understanding Users: Research & Personas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                Text(
                    text = "UX focuses on creating products that are easy to use, useful, and enjoyable. It involves understanding user needs, designing with empathy, and improving through testing and feedback.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // Download Button
                Button(
                    onClick = { /* Download action */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5E5B8C),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .padding(top = 16.dp)
                ) {
                    Text("Download Course")
                }
                
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF5E5B8C),
                    modifier = Modifier.padding(top = 24.dp),
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(4.dp)
                                .padding(horizontal = 40.dp)
                                .background(color = Color(0xFF5E5B8C), shape = RoundedCornerShape(8.dp))
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedTab == index) Color(0xFF5E5B8C) else Color.Gray
                                )
                            }
                        )
                    }
                }
                
                // Tab Content
                when (selectedTab) {
                    0 -> VideoTabContent()
                    1 -> QuizTabContent()
                }
            }
        }
    }
}

@Composable
fun VideoTabContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        LessonItem(
            title = "Understanding Users: Research & Personas",
            duration = "12:00",
            imageRes = R.drawable.social_media_marketing
        )
        Spacer(modifier = Modifier.height(4.dp))
        LessonItem(
            title = "Wireframing & Prototyping",
            duration = "15:00",
            imageRes = R.drawable.social_media_marketing
        )
        Spacer(modifier = Modifier.height(4.dp))
        LessonItem(
            title = "UI Design Principles & Visual Hierarchy",
            duration = "16:00",
            imageRes = R.drawable.social_media_marketing
        )
        Spacer(modifier = Modifier.height(4.dp))
        LessonItem(
            title = "Typography, Color, and Iconography",
            duration = "10:00",
            imageRes = R.drawable.social_media_marketing
        )
    }
}

@Composable
fun QuizTabContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        QuizItem(
            title = "Quiz: Intro to UI/UX Design Basics",
            exerciseCount = 5,
            imageRes = R.drawable.social_media_marketing
        )
        Spacer(modifier = Modifier.height(4.dp))
        QuizItem(
            title = "Quiz: Understanding Your Users",
            exerciseCount = 10,
            imageRes = R.drawable.social_media_marketing
        )
        Spacer(modifier = Modifier.height(4.dp))
        QuizItem(
            title = "Quiz: UX Principles & Journey Mapping",
            exerciseCount = 8,
            imageRes = R.drawable.social_media_marketing
        )
        Spacer(modifier = Modifier.height(4.dp))
        QuizItem(
            title = "Quiz: From Wireframe to Prototype",
            exerciseCount = 7,
            imageRes = R.drawable.social_media_marketing
        )
    }
}

@Composable
fun LessonItem(
    title: String,
    duration: String,
    imageRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        // Title and duration
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = duration,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QuizItem(
    title: String,
    exerciseCount: Int,
    imageRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        // Title and exercise count
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "$exerciseCount Exercise",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDetailScreenPreview() {
    MaterialTheme {
        CourseDetailScreen()
    }
}

