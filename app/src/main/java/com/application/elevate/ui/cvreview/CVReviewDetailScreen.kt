package com.application.elevate.ui.cvreview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.elevate.viewmodel.cvreview.CVReviewDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVReviewDetailScreen(
  navController: NavController,
  reviewId: String,
  viewModel: CVReviewDetailViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val fileDownloader = rememberFileDownloader()
  var showEditDialog by remember { mutableStateOf(false) }
  
  // Load detail when screen opens
  LaunchedEffect(reviewId) {
    viewModel.loadCVReviewDetail(reviewId)
  }
  
  // Handle success messages
  uiState.successMessage?.let { message ->
    LaunchedEffect(message) {
      // Show snackbar or toast
      viewModel.clearSuccessMessage()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("CV Review Detail") },
        navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          // Edit button
          IconButton(onClick = { showEditDialog = true }) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit")
          }
        }
      )
    }
  ) { innerPadding ->
    
    when {
      uiState.isLoading -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }
      
      uiState.error != null -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Filled.Error,
              contentDescription = null,
              modifier = Modifier.size(80.dp),
              tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = uiState.error!!,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.loadCVReviewDetail(reviewId) }) {
              Text("Retry")
            }
          }
        }
      }
      
      uiState.cvReviewData != null -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          item {
            CVDetailHeader(
              cvData = uiState.cvReviewData!!,
              onDownloadClick = {
                fileDownloader.downloadFile(
                  url = uiState.cvReviewData!!.fileUrl,
                  fileName = uiState.cvReviewData!!.fileName
                )
              }
            )
          }
          
          item {
            CVScoresSection(cvData = uiState.cvReviewData!!)
          }
          
          item {
            CVAnalysisSection(cvData = uiState.cvReviewData!!)
          }
          
          item {
            CVSuggestionsSection(cvData = uiState.cvReviewData!!)
          }
          
          item {
            Spacer(modifier = Modifier.height(24.dp))
          }
        }
      }
    }
  }
  
  // Edit Career Field Dialog
  if (showEditDialog && uiState.cvReviewData != null) {
    EditCareerFieldDialog(
      currentCareerField = uiState.cvReviewData!!.careerField,
      onDismiss = { showEditDialog = false },
      onSave = { newCareerField ->
        viewModel.updateCareerField(reviewId, newCareerField)
        showEditDialog = false
      }
    )
  }
}

@Composable
fun CVDetailHeader(
  cvData: com.application.elevate.model.CVReviewData,
  onDownloadClick: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier.padding(20.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = cvData.fileName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = cvData.careerField,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "File Size: ${cvData.fileSize / 1024} KB",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )
        }
        
        Button(
          onClick = onDownloadClick,
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
          )
        ) {
          Icon(
            Icons.Filled.Download,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Download")
        }
      }
      
      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider()
      Spacer(modifier = Modifier.height(16.dp))
      
      // Overall score display
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Overall Score",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "${cvData.scores.overallScore}%",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}

@Composable
fun CVScoresSection(cvData: com.application.elevate.model.CVReviewData) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        text = "Detailed Scores",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp)
      )
      
      val scores = listOf(
        "Relevancy Rate" to cvData.scores.relevancyRate,
        "Targeted Job Rate" to cvData.scores.targetedJobRate,
        "Relevant Skills" to cvData.scores.relevantSkill,
        "Work Experience" to cvData.scores.workExperience,
        "Consistency" to cvData.scores.consistency,
        "Writing Quality" to cvData.scores.writingQuality
      )
      
      scores.forEach { (label, score) ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
          )
          
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            LinearProgressIndicator(
              progress = (score / 100f).toFloat(),
              modifier = Modifier
                .width(80.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = when {
                score >= 80 -> MaterialTheme.colorScheme.primary
                score >= 60 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
              }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "${score.toInt()}%",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = when {
                score >= 80 -> MaterialTheme.colorScheme.primary
                score >= 60 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun CVAnalysisSection(cvData: com.application.elevate.model.CVReviewData) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        text = "AI Analysis",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp)
      )
      
      // Summary
      AnalysisItem(
        title = "Summary",
        content = cvData.aiAnalysis.summary,
        icon = Icons.Filled.Description
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      // Career Field Fit
      AnalysisItem(
        title = "Career Field Fit",
        content = cvData.aiAnalysis.careerFieldFit,
        icon = Icons.Filled.Work
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      // Strengths
      AnalysisItem(
        title = "Strengths",
        content = cvData.aiAnalysis.strengths.joinToString("\n• ", "• "),
        icon = Icons.Filled.TrendingUp
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      // Weaknesses
      AnalysisItem(
        title = "Areas for Improvement",
        content = cvData.aiAnalysis.weaknesses.joinToString("\n• ", "• "),
        icon = Icons.Filled.TrendingDown
      )
    }
  }
}

@Composable
fun CVSuggestionsSection(cvData: com.application.elevate.model.CVReviewData) {
  if (cvData.suggestions.isNotEmpty()) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp)
      ) {
        Text(
          text = "Suggestions",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        
        cvData.suggestions.forEachIndexed { index, suggestion ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp)
          ) {
            Surface(
              modifier = Modifier.size(24.dp),
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Box(
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${index + 1}",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = suggestion,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }
}

@Composable
fun AnalysisItem(
  title: String,
  content: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector
) {
  Column {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 8.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
      )
    }
    Text(
      text = content,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
      modifier = Modifier.padding(start = 28.dp)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCareerFieldDialog(
  currentCareerField: String,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit
) {
  var selectedCareerField by remember { mutableStateOf(currentCareerField) }
  var expanded by remember { mutableStateOf(false) }
  
  val careerFields = remember { 
    listOf("Software Engineer", "Marketing", "Design", "Finance", "Data Science", "Product Manager") 
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Edit Career Field") },
    text = {
      Column {
        Text(
          text = "Select a new career field for this CV review:",
          modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ExposedDropdownMenuBox(
          expanded = expanded,
          onExpandedChange = { expanded = !expanded }
        ) {
          OutlinedTextField(
            value = selectedCareerField,
            onValueChange = { },
            readOnly = true,
            label = { Text("Career Field") },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor(),
            trailingIcon = {
              ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
          )

          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            careerFields.forEach { field ->
              DropdownMenuItem(
                text = { Text(text = field) },
                onClick = {
                  selectedCareerField = field
                  expanded = false
                }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = { onSave(selectedCareerField) },
        enabled = selectedCareerField != currentCareerField
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
} 