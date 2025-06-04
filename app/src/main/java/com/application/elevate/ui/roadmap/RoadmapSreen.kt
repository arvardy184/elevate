package com.application.elevate.ui.roadmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.elevate.R
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.Course
import com.application.elevate.model.CourseWithPosition
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.ui.component.Navbar
import com.application.elevate.ui.component.RoadmapHeader
import com.application.elevate.model.User

@Composable
fun RoadmapScreen(
    uiState: RoadmapUiState,
    user: User,
    navController: NavController,
    onCourseClick: (Course) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current

        // Background
        Image(
            painter = painterResource(id = R.drawable.roadmap_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            RoadmapHeader(user)

            // Checkpoint positions
            uiState.courses.forEach { item ->
                val offsetXPx = item.percentX * screenWidthPx
                val offsetYPx = item.percentY * screenHeightPx
                val offsetX = with(density) { offsetXPx.toDp() }
                val offsetY = with(density) { offsetYPx.toDp() }

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable(enabled = item.course.progressPercent > 0) {
                            onCourseClick(item.course)
                        }
                ) {
                    Image(
                        painter = painterResource(id = item.course.imageRes),
                        contentDescription = item.course.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (item.course.progressPercent == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x77000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.White
                            )
                        }
                    }

                    val statusText = when {
                        item.course.progressPercent == 100 -> "Completed"
                        item.course.progressPercent in 1..99 -> "You're here"
                        else -> ""
                    }
                    if (statusText.isNotEmpty()) {
                        Text(
                            text = statusText,
                            color = if (statusText == "Completed") Color.Green else Color.Black,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-20).dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Navbar paling atas
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) {
            Navbar(navController = navController, onItemClick = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
        }
    }

}



@Composable
fun CourseCheckpoint(course: Course, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(enabled = !course.isLocked) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = course.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (course.isLocked) Color.Gray else Color.White)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (course.isLocked) "Locked" else course.title,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoadmapScreen() {
    val coursePositions = listOf(
        CourseWithPosition(ProfileDummyData.dummyCourses[0], 0.50f, 0.082f),
        CourseWithPosition(ProfileDummyData.dummyCourses[1], 0.63f, 0.16f),
        CourseWithPosition(ProfileDummyData.dummyCourses[2], 0.3f, 0.125f),
        CourseWithPosition(ProfileDummyData.dummyCourses[3], 0.4f, 0.225f),
        CourseWithPosition(ProfileDummyData.dummyCourses[4], 0.08f, 0.17f)
    )

    val dummyUiState = RoadmapUiState(courses = coursePositions)

    val dummyUser = User(
        id = 1,
        email = "user@example.com",
        role = "USER",
        firstName = "Keisya",
        lastName = "Setiandini",
        photoUrl = null,
        address = null,
        phoneNumber = null,
        gender = null,
        birthDate = null
    )

    RoadmapScreen(
        uiState = dummyUiState,
        user = dummyUser,
        navController = rememberNavController(),
        onCourseClick = {}
    )
}