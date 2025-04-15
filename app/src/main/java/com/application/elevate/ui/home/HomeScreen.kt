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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.component.*
import com.application.elevate.data.categories
import com.application.elevate.data.dummyCourses
import com.application.elevate.data.dummyUser
import com.application.elevate.data.growthHubItems
import com.application.elevate.model.Course
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun HomeScreen(navController: NavController,viewModel: HomeViewModel = HomeViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val currentRoute = "home"

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔻 SCROLLABLE CONTENT
        Scaffold(
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier

                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .zIndex(0f)
            ) {
                HeaderCard(
                    user = dummyUser,
                    onNotificationClick = {},
                    onFilterClick = {}
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 150.dp)
                        .zIndex(0f)// 👉 Biarkan ruang kosong buat navbar
                ) {
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

                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(title = "Categories", onViewAllClick = {})
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            CategoryChip(text = category) {
                                Log.d("CategoryChip", "Clicked: $category")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(title = "Popular Courses", onViewAllClick = {})
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(dummyCourses) { course -> CourseCard(course) }
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
            Navbar(
                selectedRoute = currentRoute,
                onItemClick = {
                    Log.d("BottomNavBar", "Navigasi ke: $it")
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ReplyTheme { // Pastikan ini adalah theme kamu
//        HomeScreen()
    }
}
