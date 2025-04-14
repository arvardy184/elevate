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
import com.application.elevate.component.ExpandableHelpItem
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.ui.theme.Neutral7
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun HelpCenterScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HelpCenterContent(
        helpItems = uiState.helpCenterItems,
        onToggleItemExpansion = { itemId -> viewModel.toggleHelpItemExpansion(itemId) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterContent(
    helpItems: List<com.application.elevate.model.HelpCenterItem>,
    onToggleItemExpansion: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Help Center") },
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
                .padding(16.dp)
        ) {
            Text(
                text = "About App",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(helpItems) { item ->
                    ExpandableHelpItem(
                        item = item,
                        onToggleExpand = { onToggleItemExpansion(item.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Still have some unanswered question?",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Send us a message elevatehelp@gmail.com",
                            fontSize = 14.sp,
                            color = Neutral7,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun HelpCenterScreenPreview() {
    ReplyTheme {
        HelpCenterContent(
            helpItems = ProfileDummyData.helpCenterItems,
            onToggleItemExpansion = {},
            onNavigateBack = {}
        )
    }
}