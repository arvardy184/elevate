package com.application.elevate.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.application.elevate.ui.theme.ReplyTheme
import com.application.elevate.ui.theme.Purple5
import com.application.elevate.ui.theme.Neutral1
import com.application.elevate.ui.theme.Neutral10
import com.application.elevate.ui.theme.Neutral7

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Search",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search here...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Filter",
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.95f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search History
        Text(
            text = "Search History",
            color = Purple5,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        uiState.searchHistory.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item,
                    color = Neutral10,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = { viewModel.removeHistoryItem(item) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Purple5
                    )
                }
            }
        }

        if (uiState.searchHistory.isNotEmpty()) {
            TextButton(
                onClick = { viewModel.showDialog(true) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Delete all",
                    color = Neutral7
                )
            }
        }
    }

    // Alert Dialog
    if (uiState.isDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialog(false) },
            title = {
                Text(
                    text = "Clear search history?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "All your search history will be deleted and cannot be restored.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearHistory() }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDialog(false) }) {
                    Text(text = "No")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    ReplyTheme {
        SearchScreen()
    }
}
