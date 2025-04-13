package com.application.elevate.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_search),
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            var query by remember { mutableStateOf("") }

            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        text = "Search Here...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Purple5, shape = CircleShape)
                    .clickable { }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_manage),
                    contentDescription = "Filter",
                    tint = Neutral1
                )
            }
        }

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
