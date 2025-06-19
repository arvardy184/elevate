package com.application.elevate.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.elevate.ui.theme.Purple5
import com.application.elevate.ui.theme.Neutral10
import com.application.elevate.viewmodel.search.SearchViewModel
import com.application.elevate.ui.component.CourseCard
import com.application.elevate.ui.component.ConsultantCard
import com.application.elevate.model.Course
import com.application.elevate.model.Consultant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onCourseClick: (Course) -> Unit = {},
    onConsultantClick: (Consultant) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Clear any error when screen starts
    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.weight(1f)
            )
            
            TextButton(
                onClick = { /* Navigate to simple search or show history */ }
            ) {
                Text(
                    text = "History",
                    color = Purple5
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onQueryChanged,
            placeholder = { Text("Search courses, consultants...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = viewModel::clearSearchQuery) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content Type Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContentTypeChip(
                text = "All",
                selected = uiState.selectedContentType == "all",
                onClick = { viewModel.selectContentType("all") }
            )
            ContentTypeChip(
                text = "Courses",
                selected = uiState.selectedContentType == "course",
                onClick = { viewModel.selectContentType("course") }
            )
            ContentTypeChip(
                text = "Consultants",
                selected = uiState.selectedContentType == "consultant",
                onClick = { viewModel.selectContentType("consultant") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Error State
        uiState.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Search Results
        LazyColumn {
            uiState.searchResults?.let { results ->
                if (results.totalResults > 0) {
                    item {
                        Text(
                            text = "${results.totalResults} results for \"${results.query}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Neutral10,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Course Results
                    if (results.courses.isNotEmpty()) {
                        item {
                            Text(
                                text = "Courses (${results.courses.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(results.courses) { course ->
                            CourseCard(
                                course = course,
                                onClick = { onCourseClick(course) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    // Consultant Results
                    if (results.consultants.isNotEmpty()) {
                        item {
                            Text(
                                text = "Consultants (${results.consultants.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(results.consultants) { consultant ->
                            ConsultantCard(
                                consultant = consultant,
                                onClick = { onConsultantClick(consultant) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                } else if (uiState.hasSearched) {
                    item {
                        EmptySearchResults(query = results.query)
                    }
                }
            } ?: run {
                // Show search history and suggestions when no search results
                if (uiState.searchHistory.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(uiState.searchHistory.take(5)) { searchHistory ->
                        SearchHistoryItem(
                            searchHistory = searchHistory,
                            onClick = { viewModel.onSearchHistorySelected(searchHistory) }
                        )
                    }
                }

                if (uiState.searchSuggestions.isNotEmpty()) {
                    item {
                        Text(
                            text = "Suggestions",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(uiState.searchSuggestions) { suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = { viewModel.onSuggestionSelected(suggestion) }
                        )
                    }
                }

                if (uiState.popularCourses.isNotEmpty()) {
                    item {
                        Text(
                            text = "Popular Courses",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(uiState.popularCourses.take(3)) { course ->
                        CourseCard(
                            course = course,
                            onClick = { onCourseClick(course) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(text) },
        selected = selected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Purple5,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun SearchHistoryItem(
    searchHistory: com.application.elevate.data.database.entity.SearchHistoryEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = searchHistory.searchQuery,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = searchHistory.searchType.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Purple5
            )
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = suggestion,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptySearchResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try different keywords or check your spelling",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral10
        )
    }
} 