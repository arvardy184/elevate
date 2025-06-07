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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.application.elevate.R
import com.application.elevate.ui.component.CategoryChip
import com.application.elevate.ui.component.Navbar
import com.application.elevate.ui.component.SavedCourseItem
import com.application.elevate.ui.component.SectionHeader
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.viewmodel.course.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    navController: NavController = rememberNavController(),
    viewModel: CourseViewModel = hiltViewModel()
) {
    val selectedCategory = remember { mutableStateOf("Design") }
    val categories = listOf("Design", "App & Web Development", "Digital Marketing", "All")
    val isViewAll = remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCourses()
    }

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
            containerColor = Color.Transparent
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
                            Button(onClick = { viewModel.fetchCourses() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(Color(0xFFF8F8F8))
                            .verticalScroll(rememberScrollState())
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            items(categories) { category ->
                                CategoryChip(
                                    text = category,
                                    onClick = { selectedCategory.value = category }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Recently Opened Section
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

                            // Jika ada course yang sedang dibuka, tampilkan di sini
                            uiState.courses.firstOrNull()?.let { recentCourse ->
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
                                        AsyncImage(
                                            model = recentCourse.thumbnail,
                                            contentDescription = recentCourse.title,
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
                                                        text = recentCourse.title,
                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    )
                                                    Text(
                                                        text = recentCourse.category.name,
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
                                        }
                                    }
                                }
                            }
                        }

                        // Saved Courses Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                        ) {
                            SectionHeader(
                                title = "Saved Course",
                                onViewAllClick = { isViewAll.value = !isViewAll.value }
                            )

                            val coursesToShow = if (isViewAll.value) uiState.courses else uiState.courses.take(3)

                            coursesToShow.forEach { course ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .shadow(4.dp)
                                        .clickable { navController.navigate("course_detail/${course.id}") },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = course.thumbnail,
                                            contentDescription = course.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = course.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                            Text(
                                                text = course.category.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray
                                            )
                                            if (course.isPaid) {
                                                Text(
                                                    text = "Rp ${course.price}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            } else {
                                                Text(
                                                    text = "Gratis",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = "Open Course",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
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

