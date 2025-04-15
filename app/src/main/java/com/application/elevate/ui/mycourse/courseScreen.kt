package com.application.elevate.ui.mycourse

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R
import com.application.elevate.component.Navbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(navController: NavController = rememberNavController()) {
    val selectedCategory = remember { mutableStateOf("Design") }
    val categories = listOf("Design", "App & Web Development", "Digital Marketing", "All")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Course",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4A7F)
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
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.Gray
            ) {
                Navbar(
                    selectedRoute = "course",
                    onItemClick = {

                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .verticalScroll(rememberScrollState())
        ) {
            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory.value == category,
                        onClick = { selectedCategory.value = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF5E5B8C),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFE9E8F2)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
            
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
                
                // Recently Opened Course Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column {
                        // Course Image with overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner), // Replace with actual course image
                                contentDescription = "UI/UX Design",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Purple overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x551E1E3F))
                            )
                            
                            // Course title and details
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
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
                            
                            // Arrow button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
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
                        
                        // Progress bar
                        LinearProgressIndicator(
                            progress = 0.55f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = Color(0xFFE37777),
                            trackColor = Color(0xFFEEEEEE)
                        )
                    }
                }
            }
            
            // Saved Courses Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Courses",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    )
                    TextButton(onClick = { /* View All action */ }) {
                        Text(
                            text = "View All",
                            color = Color(0xFF635C9C)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View All",
                            tint = Color(0xFF635C9C),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                // Saved Course Items
                SavedCourseItem(
                    title = "Social Media Marketing",
                    duration = "2hr20min",
                    lessons = 10,
                    rating = 4.5f,
                    ratingCount = 140,
                    imageResId = R.drawable.social_media_marketing // Replace with actual course image
                )
                
                SavedCourseItem(
                    title = "Android Development",
                    duration = "3hr15min",
                    lessons = 13,
                    rating = 5.0f,
                    ratingCount = 201,
                    imageResId = R.drawable.android_development // Replace with actual course image
                )
                
                SavedCourseItem(
                    title = "Basic Accounting",
                    duration = "2hr04min",
                    lessons = 11,
                    rating = 4.0f,
                    ratingCount = 96,
                    imageResId = R.drawable.basic_accounting // Replace with actual course image
                )
            }
            
            // Add some bottom padding to account for the bottom navigation
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.painter.Painter,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF5E5B8C) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) Color(0xFF5E5B8C) else Color.Gray
        )
    }
}

@Composable
fun SavedCourseItem(
    title: String,
    duration: String,
    lessons: Int,
    rating: Float,
    ratingCount: Int,
    imageResId: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Course Image
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            // Course Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.mingcute_timeline), // Replace with clock icon
                        contentDescription = "Duration",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$duration • $lessons Lessons",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                // Star Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        val starColor = if (index < rating.toInt()) Color(0xFFFFD700)
                        else if (index < rating && index >= rating.toInt()) Color(0xFFFFD700) // Half star
                        else Color.LightGray
                        
                        Icon(
                            painter = painterResource(id = R.drawable.star), // Replace with star icon
                            contentDescription = "Star",
                            tint = starColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Text(
                        text = "($ratingCount)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            // Navigation Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9E8F2))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Open Course",
                    tint = Color(0xFF5E5B8C),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(16.dp)
                )
            }
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

