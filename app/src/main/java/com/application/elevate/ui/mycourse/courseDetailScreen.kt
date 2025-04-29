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

    @Composable
    fun CourseDetailScreen(courseDetail: CourseDetail,  onBackClick: () -> Unit) {
        var selectedTab by remember { mutableStateOf("Video") }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {


            IconButton(onClick = { onBackClick()}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Gambar Header
            Image(
                painter = painterResource(id = courseDetail.imageRes),
                contentDescription = "Course Banner",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Info Course
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = courseDetail.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = courseDetail.subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = courseDetail.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF5E5B8C))
                        .clickable { /* Download action */ }
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                ) {
                    Text("Download Course", color = Color.White, fontSize = 12.sp)
                }
            }

            // Tab Video & Quiz
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                TabButton("Video", selectedTab == "Video") { selectedTab = "Video" }
                Spacer(modifier = Modifier.width(16.dp))
                TabButton("Quiz", selectedTab == "Quiz") { selectedTab = "Quiz" }
            }

            // Content List
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedTab == "Video") {
                    courseDetail.videoList.forEach {
                        VideoItemView(it)
                    }
                } else {
                    courseDetail.quizList.forEach {
                        QuizItemView(it)
                    }
                }
            }
        }
    }

    @Composable
    fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(20.dp)
                        .background(Color.Black)
                )
            }
        }
    }



    @Composable
    @Preview(showBackground = true)
    fun CourseDetailScreenPreview() {
        val courseDetail = dummyCourseDetails.first()

        CourseDetailScreen(courseDetail = courseDetail, onBackClick = {})
    }
