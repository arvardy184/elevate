package com.application.elevate.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.ui.component.Navbar
import com.application.elevate.ui.component.ProfileHeader
import com.application.elevate.ui.component.ProfileMenuItem
import com.application.elevate.ui.theme.ReplyTheme
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.model.User
import com.application.elevate.viewmodel.profile.NavigationEvent
import com.application.elevate.viewmodel.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is NavigationEvent.NavigateToLogin -> {
                navController.navigate("login_page") {
                    popUpTo("profile") { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            null -> {}
        }
    }

    ProfileScreenContent(
        navController = navController,
        uiState = uiState,
        onProfileClick = { navController.navigate("edit_profile") },
        onProfileSettingsClick = { navController.navigate("edit_profile") },
        onYourActivityClick = { navController.navigate("your_activity") },
        onMyCVReviewClick = { navController.navigate("cv_review_list") },
        onMyJobMatchingClick = { navController.navigate("job_matching_history") },
        onNotificationClick = { navController.navigate("notifications") },
        onHelpCenterClick = { navController.navigate("help_center") },
        onMyAssessmentClick = { navController.navigate("my_assessment") },
        onLogoutClick = { viewModel.logout() }
    )
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    navController: NavController,
    onProfileClick: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onYourActivityClick: () -> Unit,
    onMyCVReviewClick: () -> Unit,
    onMyJobMatchingClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onHelpCenterClick: () -> Unit,
    onMyAssessmentClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Navbar(
                navController = navController,onItemClick = { route -> navController.navigate(route) }

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Title Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ){
                ProfileHeader(
                    user = uiState.user,
                    onProfileClick = onProfileClick
                )
            }
            // Profile Header


            Spacer(modifier = Modifier.height(24.dp))

            // Account Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Account",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = "Profile Settings",
                            subtitle = "Customize your personal information",
                            onClick = onProfileSettingsClick
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Security,
                            title = "Privacy & Safety",
                            subtitle = "Manage your protection and security settings",
                            onClick = { /* Handle click */ }
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.History,
                            title = "Your Activity",
                            subtitle = "View your activity history",
                            onClick = onYourActivityClick
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Description,
                            title = "My CV Review",
                            subtitle = "Review your CV",
                            onClick = onMyCVReviewClick
                        )

                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            title = "Log Out",
                            subtitle = "Sign out from Elevate",
                            iconTint = Color(0xFF8A302E),
                            iconBackground = Color(0xFFFFC7C7),
                            onClick = onLogoutClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Others Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Others",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.Assignment,
                            title = "My Assessment",
                            subtitle = "View your assessment results and history",
                            onClick = onMyAssessmentClick
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Work,
                            title = "My Job & Skill Matching",
                            subtitle = "View your job matching history and results",
                            onClick = onMyJobMatchingClick
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Notification",
                            subtitle = "Manage your notification settings",
                            onClick = onNotificationClick
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Help,
                            title = "Help Center",
                            subtitle = "Get assistance and guidance on using the app",
                            onClick = onHelpCenterClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    val dummyNavController = rememberNavController()

    ReplyTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(user = User(
                id = 0,
                firstName = "Guest",
                lastName = "User",
                email = "guest@example.com", 
                photoUrl = "",
                address = "Default Address",
                phoneNumber = "+62 000-0000-0000",
                gender = "Unspecified",
                birthDate = "01/01/2000",
                role = "user",
                isAssessmentCompleted = false
            )
            ),
            navController = dummyNavController,
            onProfileClick = {},
            onProfileSettingsClick = {},
            onYourActivityClick = {},
            onMyCVReviewClick = {},
            onMyJobMatchingClick = {},
            onNotificationClick = {},
            onHelpCenterClick = {},
            onMyAssessmentClick = {},
            onLogoutClick = {}
        )
    }
}