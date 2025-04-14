package com.application.elevate.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.component.SettingsToggle
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.NotificationSetting
import com.application.elevate.ui.theme.Neutral5
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun NotificationScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationContent(
        notificationSettings = uiState.notificationSettings,
        onToggleSetting = { settingId -> viewModel.toggleNotificationSetting(settingId) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationContent(
    notificationSettings: List<NotificationSetting>,
    onToggleSetting: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notification") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // General Settings
            item {
                Text(
                    text = "General",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(notificationSettings.filter { it.category == "general" }) { setting ->
                SettingsToggle(
                    title = setting.title,
                    isChecked = setting.isEnabled,
                    onCheckedChange = { onToggleSetting(setting.id) }
                )
            }

            item {
                Divider(
                    color = Neutral5,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // System & Service Updates
            item {
                Text(
                    text = "System & Service updates",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(notificationSettings.filter { it.category == "system" }) { setting ->
                SettingsToggle(
                    title = setting.title,
                    isChecked = setting.isEnabled,
                    onCheckedChange = { onToggleSetting(setting.id) }
                )
            }

            item {
                Divider(
                    color = Neutral5,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // More Settings
            item {
                Text(
                    text = "More Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(notificationSettings.filter { it.category == "more" }) { setting ->
                SettingsToggle(
                    title = setting.title,
                    isChecked = setting.isEnabled,
                    onCheckedChange = { onToggleSetting(setting.id) }
                )
            }
        }
    }
}

@Preview
@Composable
fun NotificationScreenPreview() {
    ReplyTheme {
        NotificationContent(
            notificationSettings = ProfileDummyData.notificationSettings,
            onToggleSetting = {},
            onNavigateBack = {}
        )
    }
}