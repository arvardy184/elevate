package com.application.elevate.ui.home


import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.component.*
import com.application.elevate.data.dummy.ProfileDummyData.categories
import com.application.elevate.data.dummy.ProfileDummyData.currentUser
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourses
import com.application.elevate.data.dummy.ProfileDummyData.growthHubItems
import com.application.elevate.model.Course
import com.application.elevate.ui.theme.ReplyTheme


@Composable
fun HomeScreen(navController: NavController,viewModel: HomeViewModel = HomeViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val density = LocalDensity.current


    var tutorialStep by remember { mutableStateOf(0) }
    val showTutorial = tutorialStep in 0..3

    var searchBarPosition by remember { mutableStateOf(Offset.Zero) }
    var searchBarSize by remember { mutableStateOf(Size.Zero) }

    var growthHubPosition by remember { mutableStateOf(Offset.Zero) }
    var growthHubSize by remember { mutableStateOf(Size.Zero) }

    var categoryPosition by remember { mutableStateOf(Offset.Zero) }
    var categorySize by remember { mutableStateOf(Size.Zero) }

    var counselingButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var counselingButtonSize by remember { mutableStateOf(Size.Zero) }

    var popularCoursePosition by remember { mutableStateOf(Offset.Zero) }
    var popularCourseSize by remember { mutableStateOf(Size.Zero) }




    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HeaderCard(
                    user = currentUser,
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
                        SectionHeader(title = "Growth Hub", onViewAllClick = {})
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(growthHubItems) { item ->
                                GrowthHubItem(label = item.title, imageRes = item.imageRes) {
                                    Log.d("GrowthHubItem", "Clicked: ${item.title}")
                                    if(item.title == "CV Review"){
//                                    Log.d("Cek cv review","click": )
                                        navController.navigate("cv_review")
                                    }
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            categoryPosition = coordinates.positionInRoot()
                            categorySize = coordinates.size.toSize()}) {

                        SectionHeader(title = "Categories", onViewAllClick = {})
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { category ->
                                CategoryChip(text = category) {
                                    Log.d("CategoryChip", "Clicked: $category")
                                }
                            }
                        }

                    }



                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            popularCoursePosition = coordinates.positionInRoot()
                            popularCourseSize = coordinates.size.toSize()}) {

                        SectionHeader(title = "Popular Courses", onViewAllClick = {})
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(dummyCourses) { course -> CourseCard(course) }
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


    // >>>> Tambahan Tutorial Overlay

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
                    message = "Pick a topic you’re interested in and start learning right away!",
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
                    message = "Check out our most popular courses and boost your skills today!",
                    onNext = { tutorialStep++ }
                )
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ReplyTheme { // Pastikan ini adalah theme kamu
        HomeScreen(navController = rememberNavController())
    }
}
