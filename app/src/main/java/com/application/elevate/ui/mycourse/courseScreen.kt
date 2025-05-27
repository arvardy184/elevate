package com.application.elevate.ui.mycourse

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R
import com.application.elevate.component.CategoryChip
import com.application.elevate.component.HeaderCard
import com.application.elevate.component.Navbar
import com.application.elevate.component.SavedCourseItem
import com.application.elevate.component.SectionHeader
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourses

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(navController: NavController = rememberNavController()) {
    val selectedCategory = remember { mutableStateOf("Design") }
    val categories = listOf("Design", "App & Web Development", "Digital Marketing", "All")
    val isViewAll = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "My Course",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A4A7F),
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Search action */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color(0xFF4A4A7F)
                    )
                )
            },
            containerColor = Color.Transparent // agar tidak menutupi background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8))
                    .verticalScroll(rememberScrollState())
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 16.dp)) {
                    items(ProfileDummyData.categories) { category ->
                        CategoryChip(text = category) {
                            Log.d("CategoryChip", "Clicked: $category")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Recently Opened",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .shadow(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner),
                                contentDescription = "UI/UX Design",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 45.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Text(
                                            text = "UI/UX Design",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Part 1: Design Principle • 10 Min",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White
                                            )
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF635C9C))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Continue",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .size(20.dp)
                                        )
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { 0.3f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .padding(horizontal = 20.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFF65558F),
                                    trackColor = Color(0xFFEEEEEE),
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    SectionHeader(title = "Saved Course", onViewAllClick = {
                        isViewAll.value = !isViewAll.value
                    })

                    val coursesToShow = if (isViewAll.value) dummyCourses else dummyCourses.take(3)

                    coursesToShow.forEach { course ->
                        SavedCourseItem(
                            course = course,
                            onClick = { navController.navigate("course_detail/${course.id}") }
                        )
                    }
                }

                // Spacer untuk memberi ruang di bawah agar konten tidak tertutup navbar
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Navbar ditumpuk di bawah layar, menimpa konten
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f) // pastikan di atas konten
        ) {
            Navbar(
                navController = navController,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CourseScreenPreview() {
    MaterialTheme {
        CourseScreen()
    }
}

