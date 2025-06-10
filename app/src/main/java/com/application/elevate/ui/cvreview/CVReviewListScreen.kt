package com.application.elevate.ui.cvreview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.elevate.model.CVReviewItem
import com.application.elevate.viewmodel.cvreview.CVReviewListViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVReviewListScreen(
  navController: NavController,
  viewModel: CVReviewListViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  
  // Show success/error messages
  uiState.successMessage?.let { message ->
    LaunchedEffect(message) {
      // Could show snackbar here
      viewModel.clearSuccessMessage()
    }
  }
  
  uiState.error?.let { error ->
    LaunchedEffect(error) {
      // Could show snackbar here
      viewModel.clearError()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { 
          Column {
            Text("My CV Reviews")
            if (uiState.isOffline) {
              Text(
                text = "Offline Mode",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.refreshCVReviews() }) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
          }
        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { navController.navigate("cv_review") }
      ) {
        Icon(Icons.Filled.Add, contentDescription = "New CV Review")
      }
    }
  ) { innerPadding ->
    
    when {
      uiState.isLoading && uiState.cvReviews.isEmpty() -> {
        // Initial loading
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }
      
      uiState.cvReviews.isEmpty() && !uiState.isLoading -> {
        // Empty state
        EmptyStateContent(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
          onCreateNewClick = { navController.navigate("cv_review") }
        )
      }
      
      else -> {
        // Content with list
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(uiState.cvReviews) { cvReview ->
            CVReviewCard(
              cvReview = cvReview,
              onItemClick = { navController.navigate("cv_review_detail/${cvReview.id}") },
              onDeleteClick = { viewModel.deleteCVReview(cvReview.id) }
            )
          }
          
          // Load more button
          if (uiState.currentPage < uiState.pagination.totalPages && !uiState.isOffline) {
            item {
              LoadMoreButton(
                isLoading = uiState.isLoading,
                onClick = { viewModel.loadMoreCVReviews() }
              )
            }
          }
          
          // Bottom padding for FAB
          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      }
    }
  }
}

@Composable
fun CVReviewCard(
  cvReview: CVReviewItem,
  onItemClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  var showDeleteDialog by remember { mutableStateOf(false) }
  
  Card(
    modifier = Modifier.fillMaxWidth(),
    onClick = onItemClick,
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = cvReview.fileName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = cvReview.careerField,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = formatDate(cvReview.createdAt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }
        
        IconButton(onClick = { showDeleteDialog = true }) {
          Icon(
            Icons.Filled.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error
          )
        }
      }
      
      Spacer(modifier = Modifier.height(12.dp))
      
      // Score indicators
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        ScoreIndicator(
          label = "Overall",
          score = cvReview.overallScore,
          modifier = Modifier.weight(1f)
        )
        ScoreIndicator(
          label = "Relevancy",
          score = cvReview.relevancyRate,
          modifier = Modifier.weight(1f)
        )
        ScoreIndicator(
          label = "Target Job",
          score = cvReview.targetedJobRate,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
  
  // Delete confirmation dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("Delete CV Review") },
      text = { Text("Are you sure you want to delete this CV review? This action cannot be undone.") },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteClick()
            showDeleteDialog = false
          }
        ) {
          Text("Delete", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun ScoreIndicator(
  label: String,
  score: Double,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "${score.toInt()}%",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = when {
        score >= 80 -> MaterialTheme.colorScheme.primary
        score >= 60 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
      }
    )
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
  }
}

@Composable
fun LoadMoreButton(
  isLoading: Boolean,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    enabled = !isLoading
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(16.dp),
        color = MaterialTheme.colorScheme.onPrimary
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(if (isLoading) "Loading..." else "Load More")
  }
}

@Composable
fun EmptyStateContent(
  modifier: Modifier = Modifier,
  onCreateNewClick: () -> Unit
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      Icons.Filled.Description,
      contentDescription = null,
      modifier = Modifier.size(80.dp),
      tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "No CV Reviews Yet",
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Upload your CV to get detailed analysis and feedback",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
    Spacer(modifier = Modifier.height(24.dp))
    PrimaryButton(
      text = "Upload Your First CV",
      onClick = onCreateNewClick
    )
  }
}

private fun formatDate(dateString: String): String {
  return try {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    outputFormat.format(date ?: Date())
  } catch (e: Exception) {
    dateString
  }
} 