package com.application.elevate.ui.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.application.elevate.data.dummy.ProfileDummyData.categories
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourses
import com.application.elevate.data.dummy.ProfileDummyData.growthHubItems
import com.application.elevate.model.User
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.elevate.ui.component.CategoryChip
import com.application.elevate.ui.component.CourseCard
import com.application.elevate.ui.component.GrowthHubItem
import com.application.elevate.ui.component.HeaderCard
import com.application.elevate.ui.component.Navbar
import com.application.elevate.ui.component.SectionHeader
import com.application.elevate.ui.component.TutorialOverlay
import com.application.elevate.viewmodel.home.HomeViewModel
import com.application.elevate.R
import com.application.elevate.model.Course
import com.application.elevate.model.GrowthHub

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val density = LocalDensity.current

    // Refresh data user saat screen muncul
    LaunchedEffect(Unit) {
        viewModel.refreshUserData()
    }

    // Gunakan user dari uiState dengan default user jika null
    val user = uiState.user ?: User(
        id = 0,
        firstName = "Guest",
        lastName = "User",
        email = "guest@example.com",
        photoUrl = "",
        address = "Default Address",
        phoneNumber = "+62 000-0000-0000",
        gender = "Unspecified",
        birthDate = "01/01/2000",
        role = "USER",
        isAssessmentCompleted = false
    )

    var tutorialStep by remember { mutableStateOf(0) }
    val showTutorial = tutorialStep in 0..3 && uiState.showTutorial

    var searchBarPosition by remember { mutableStateOf(Offset.Zero) }
    var searchBarSize by remember { mutableStateOf(Size.Zero) }

    var growthHubPosition by remember { mutableStateOf(Offset.Zero) }
    var growthHubSize by remember { mutableStateOf(Size.Zero) }

    var categoryPosition by remember { mutableStateOf(Offset.Zero) }
    var categorySize by remember { mutableStateOf(Size.Zero) }

    var popularCoursePosition by remember { mutableStateOf(Offset.Zero) }
    var popularCourseSize by remember { mutableStateOf(Size.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HeaderCard(
                    user = user,
                    onNotificationClick = { navController.navigate("notification")},
                    onSearchClick = {Log.d("HeaderCard", "Search clicked")
                        navController.navigate("search")  },
                    onSearchBarPositioned = { position, size ->
                        searchBarPosition = position
                        searchBarSize = size
                    },
                    modifier = Modifier.background(Color.White)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 150.dp)
                        .zIndex(0f)// 👉 Biarkan ruang kosong buat navbar
                ) {
                    Column(modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            growthHubPosition = coordinates.positionInRoot()
                            growthHubSize = coordinates.size.toSize()}) {
                        Text(
                            text = "Growth Hub",
                            style = MaterialTheme.typography.titleMedium
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(viewModel.getGrowthHubItems()) { item ->
                                GrowthHubItem(
                                    label = item.title,
                                    imageRes = item.imageRes,
                                    onClick = {
                                        if(item.title == "CV Review"){
                                            navController.navigate("cv_review")
                                        }
                                        if(item.title == "Counseling"){
                                            navController.navigate("consultant")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            categoryPosition = coordinates.positionInRoot()
                            categorySize = coordinates.size.toSize()}) {
                        SectionHeader(
                            title = "Categories",
                            onViewAllClick = { navController.navigate("categories") }
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(viewModel.getCategories()) { category ->
                                CategoryChip(
                                    text = category,
                                    onClick = { /* TODO: Filter by category */ }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            popularCoursePosition = coordinates.positionInRoot()
                            popularCourseSize = coordinates.size.toSize()}) {
                        SectionHeader(
                            title = "Recommended Courses",
                            onViewAllClick = { /* TODO: Navigate to course list */ }
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(viewModel.getRecommendedCourses()) { course ->
                                CourseCard(
                                    course = course,
                                    onClick = { selectedCourse ->
                                        navController.navigate("course_detail/${selectedCourse.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 🔺 FIXED NAVBAR OVERLAY
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 80.dp)
                .zIndex(1f)
        ) {
            Navbar(navController = navController,     onItemClick = { navController.navigate("home") }
            )
        }
    }

    // Tutorial Overlay Logic
    if (showTutorial) {
        when (tutorialStep) {
            0 -> {
                val rect = Rect(
                    offset = searchBarPosition,
                    size = searchBarSize
                )

                TutorialOverlay(
                    highlightRect = rect,
                    message = "Looking for something? Start typing to find courses, categories, or topics you love.",
                    onNext = { tutorialStep++ }
                )
            }
            1 -> {
                val rect = Rect(
                    offset = growthHubPosition,
                    size = growthHubSize
                )

                TutorialOverlay(
                    highlightRect = rect,
                    message = "Explore tools that help you grow — from career counseling to Job & Skill Matching",
                    onNext = { tutorialStep++ }
                )
            }
            2 -> {
                val rect = Rect(
                    offset = categoryPosition,
                    size = categorySize
                )

                TutorialOverlay(
                    highlightRect = rect,
                    message = "Browse courses by categories that interest you.",
                    onNext = { tutorialStep++ }
                )
            }
            3 -> {
                val rect = Rect(
                    offset = popularCoursePosition,
                    size = popularCourseSize
                )

                TutorialOverlay(
                    highlightRect = rect,
                    message = "These are our most popular courses — take a look!",
                    onNext = { 
                        tutorialStep = -1
                        viewModel.onTutorialComplete()
                    }
                )
            }
        }
    }
}



@Composable
private fun MenuSection(
    items: List<GrowthHub>,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .onGloballyPositioned { coordinates ->
                onPositioned(
                    coordinates.positionInRoot(),
                    coordinates.size.toSize()
                )
            }
    ) {
        Text(
            text = "Menu Utama",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                MenuCard(item = item)
            }
        }
    }
}

@Composable
private fun MenuCard(item: GrowthHub) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CourseListSection(
    courses: List<Course>,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .onGloballyPositioned { coordinates ->
                onPositioned(
                    coordinates.positionInRoot(),
                    coordinates.size.toSize()
                )
            }
    ) {
        Text(
            text = "Kursus Tersedia",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        courses.forEach { course ->
            CourseCard(course = course)
        }
    }
}

@Composable
private fun CourseCard(course: Course) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${course.duration} • ${course.lessons} pelajaran",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorSection(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Coba Lagi")
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_empty_state),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message)
    }
}

@Composable
private fun OfflineBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    ) {
        Text(
            text = "Anda sedang offline. Beberapa fitur mungkin tidak tersedia.",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun TutorialOverlay(
    highlightRect: Rect,
    message: String,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onNext),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    ReplyTheme {
//        HomeScreen(
//            navController = rememberNavController(),
//            viewModel = PreviewHomeViewModel()
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenWithTutorialPreview() {
//    ReplyTheme {
//        val previewViewModel = PreviewHomeViewModel()
//        previewViewModel.onTutorialComplete() // Ini akan mengatur showTutorial ke false
//
//        HomeScreen(
//            navController = rememberNavController(),
//            viewModel = previewViewModel
//        )
//    }
//}
