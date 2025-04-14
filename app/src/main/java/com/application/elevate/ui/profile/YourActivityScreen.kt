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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.component.ActivityCard
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.Activity
import com.application.elevate.ui.theme.Neutral5
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun YourActivityScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    YourActivityContent(
        selectedTab = uiState.selectedTab,
        activities = uiState.activities,
        onTabSelected = { tab -> viewModel.setSelectedTab(tab) },
        onNavigateBack = onNavigateBack,
        onActivityClick = { /* Handle activity click */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourActivityContent(
    selectedTab: String,
    activities: List<Activity>,
    onTabSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onActivityClick: (Activity) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Activity") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                TabButton(
                    text = "Certificate",
                    isSelected = selectedTab == "Certificate",
                    onClick = { onTabSelected("Certificate") },
                    modifier = Modifier.weight(1f)
                )

                TabButton(
                    text = "Course",
                    isSelected = selectedTab == "Course",
                    onClick = { onTabSelected("Course") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on selected tab
            if (selectedTab == "Certificate") {
                Text(
                    text = "Download",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activities.filter { it.type == "certificate" }) { activity ->
                        ActivityCard(
                            activity = activity,
                            onClick = { onActivityClick(activity) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activities.filter { it.type == "course" }) { activity ->
                        ActivityCard(
                            activity = activity,
                            onClick = { onActivityClick(activity) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onBackground
    } else {
        Neutral5
    }

    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                )
        )
    }
}

@Preview
@Composable
fun YourActivityScreenPreview() {
    ReplyTheme {
        YourActivityContent(
            selectedTab = "Certificate",
            activities = ProfileDummyData.activities,
            onTabSelected = {},
            onNavigateBack = {},
            onActivityClick = {}
        )
    }
}