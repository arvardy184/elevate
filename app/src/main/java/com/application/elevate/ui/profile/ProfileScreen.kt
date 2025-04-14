package com.application.elevate.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.R
import com.application.elevate.component.Navbar
import com.application.elevate.component.ProfileHeader
import com.application.elevate.component.ProfileMenuItem
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.ui.theme.Neutral5
import com.application.elevate.ui.theme.Orange5
import com.application.elevate.ui.theme.ReplyTheme
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToYourActivity: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreenContent(
        uiState = uiState,
        onProfileClick = onNavigateToEditProfile,
        onProfileSettingsClick = onNavigateToEditProfile,
        onYourActivityClick = onNavigateToYourActivity,
        onNotificationClick = onNavigateToNotification,
        onHelpCenterClick = onNavigateToHelpCenter,
        onLogoutClick = onLogout
    )
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onProfileClick: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onYourActivityClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onHelpCenterClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Navbar(
                selectedRoute = "profile",
                onItemClick = { /* Handle navigation */ }
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
            // Profile Header
            ProfileHeader(
                user = uiState.user,
                onProfileClick = onProfileClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Account Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
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
                            icon = Icons.Default.ExitToApp,
                            title = "Log Out",
                            subtitle = "Sign out from Elevate",
                            iconTint = Orange5,
                            iconBackground = Orange5.copy(alpha = 0.1f),
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Column {
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
    ReplyTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(user = ProfileDummyData.currentUser),
            onProfileClick = {},
            onProfileSettingsClick = {},
            onYourActivityClick = {},
            onNotificationClick = {},
            onHelpCenterClick = {},
            onLogoutClick = {}
        )
    }
}