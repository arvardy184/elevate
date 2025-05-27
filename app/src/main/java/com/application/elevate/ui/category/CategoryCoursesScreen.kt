package com.application.elevate.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.component.CourseCard
import com.application.elevate.component.Navbar
import com.application.elevate.model.Course
import com.application.elevate.ui.theme.ReplyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCoursesScreen(
    categoryId: String,
    viewModel: CategoryCoursesViewModel = viewModel(factory = CategoryCoursesViewModelFactory(categoryId)),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Toolbar dengan tombol back dan judul kategori
            TopAppBar(
                title = {
                    Text(
                        text = uiState.categoryName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
            
            // Search Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFF3F2F7))
            ) {
                // Search icon
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                )
                
                // Text field
                BasicTextField(
                    value = searchText,
                    onValueChange = { 
                        searchText = it
                        viewModel.searchCourses(it)
                    },
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 56.dp, end = 16.dp)
                        .align(Alignment.Center)
                )
                
                // Hint text
                if (searchText.isEmpty()) {
                    Text(
                        text = "Cari kursus...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 56.dp, end = 16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            
            // Daftar kursus
            if (uiState.filteredCourses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada kursus untuk kategori ini",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(bottom = 80.dp, start = 16.dp, end = 16.dp) // Untuk navbar dan padding tepi
                ) {
                    // Group courses into pairs and display in rows
                    uiState.filteredCourses.chunked(2).forEach { rowCourses ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowCourses.forEach { course ->
                                CourseCard(
                                    course = course,
                                    onClick = { selectedCourse ->
                                        // Navigasi ke detail kursus
                                        navController.navigate("course_detail/${selectedCourse.id}")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            // Add empty space if there's an odd number of courses
                            if (rowCourses.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        
        // Bottom navbar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Navbar(
                navController = navController,
                onItemClick = { }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CategoryCoursesScreenPreview() {
    ReplyTheme {
        CategoryCoursesScreen(
            categoryId = "1",
            navController = rememberNavController()
        )
    }
}

@Preview(showBackground = true, name = "Design Category Preview", widthDp = 360, heightDp = 800)
@Composable
fun DesignCategoryCoursesScreenPreview() {
    val courses = listOf(
        Course(
            id = "1",
            title = "UI/UX Design",
            duration = "2h 45min",
            lessons = 9,
            progressPercent = 80,
            rating = 4.5f,
            ratingCount = 120,
            imageRes = com.application.elevate.R.drawable.ui_ux
        ),
        Course(
            id = "2",
            title = "Graphic Design",
            duration = "3h 20min",
            lessons = 12,
            progressPercent = 0,
            rating = 4.2f,
            ratingCount = 98,
            imageRes = com.application.elevate.R.drawable.front_end,
            categoryId = "1"
        ),
        Course(
            id = "3",
            title = "Motion Graphics",
            duration = "4h 10min",
            lessons = 15,
            progressPercent = 25,
            rating = 4.7f,
            ratingCount = 135,
            imageRes = com.application.elevate.R.drawable.android_development,
            categoryId = "1"
        )
    )
    
    val viewModel = CategoryCoursesViewModel("1")
    viewModel.updateUiState(
        CategoryCoursesUiState(
            categoryName = "Design",
            courses = courses,
            filteredCourses = courses
        )
    )
    
    ReplyTheme {
        CategoryCoursesScreen(
            categoryId = "1",
            viewModel = viewModel,
            navController = rememberNavController()
        )
    }
}

@Preview(showBackground = true, name = "Empty State Preview", widthDp = 360, heightDp = 800)
@Composable
fun EmptyCategoryCoursesScreenPreview() {
    val viewModel = CategoryCoursesViewModel("3")
    viewModel.updateUiState(
        CategoryCoursesUiState(
            categoryName = "Personal Branding",
            courses = emptyList(),
            filteredCourses = emptyList()
        )
    )
    
    ReplyTheme {
        CategoryCoursesScreen(
            categoryId = "3",
            viewModel = viewModel,
            navController = rememberNavController()
        )
    }
}

@Preview(showBackground = true, name = "Search Results Preview", widthDp = 360, heightDp = 800)
@Composable
fun SearchCategoryCoursesScreenPreview() {
    val allCourses = listOf(
        Course(
            id = "1",
            title = "UI/UX Design",
            duration = "2h 45min",
            lessons = 9,
            progressPercent = 80,
            rating = 4.5f,
            ratingCount = 120,
            imageRes = com.application.elevate.R.drawable.ui_ux
        ),
        Course(
            id = "2",
            title = "Graphic Design",
            duration = "3h 20min",
            lessons = 12,
            progressPercent = 0,
            rating = 4.2f,
            ratingCount = 98,
            imageRes = com.application.elevate.R.drawable.front_end
        ),
        Course(
            id = "3",
            title = "Web Design Fundamentals",
            duration = "4h 10min",
            lessons = 15,
            progressPercent = 30,
            rating = 4.7f,
            ratingCount = 156,
            imageRes = com.application.elevate.R.drawable.android_development
        )
    )
    
    val filteredCourses = listOf(
        Course(
            id = "1",
            title = "UI/UX Design",
            duration = "2h 45min",
            lessons = 9,
            progressPercent = 80,
            rating = 4.5f,
            ratingCount = 120,
            imageRes = com.application.elevate.R.drawable.ui_ux
        )
    )
    
    val viewModel = CategoryCoursesViewModel("1")
    viewModel.updateUiState(
        CategoryCoursesUiState(
            categoryName = "Design",
            courses = allCourses,
            filteredCourses = filteredCourses,
            searchQuery = "UI"
        )
    )
    
    ReplyTheme {
        CategoryCoursesScreen(
            categoryId = "1",
            viewModel = viewModel,
            navController = rememberNavController()
        )
    }
} 