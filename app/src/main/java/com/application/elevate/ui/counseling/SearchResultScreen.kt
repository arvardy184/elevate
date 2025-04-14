package com.application.elevate.ui.counseling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.application.elevate.component.ConsultantCard
import com.application.elevate.model.Consultant
import com.application.elevate.ui.theme.ReplyTheme
import com.application.elevate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    uiState: CounselingUiState,
    onConsultantClick: (Consultant) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Counseling") },
            navigationIcon = {
                IconButton(onClick = { /* TODO: navBack */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { /* viewModel.search(it) */ },
            placeholder = { Text("Search Consultant") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
//            colors = TextFieldDefaults.colors(
//                TextFieldColors(
//
//                )
            //)
        )

        Text(
            text = "Results for \"${uiState.searchQuery}\"",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.consultants) { c ->
                ConsultantCard(
                    consultant = c,
                    onClick = { onConsultantClick(c) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchResultScreen() {
    val dummyState = CounselingUiState(
        searchQuery = "Sarah",
        consultants = listOf(
            Consultant("1", "Sarah S.E., M.Ak.", "Certified Public Accountant (CPA)", 5f, 211, 30000, R.drawable.counseling)
        )
    )
    ReplyTheme {
        SearchResultScreen(uiState = dummyState, onConsultantClick = {})
    }
}
