package com.application.elevate.ui.cvreview

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.util.Log
import com.application.elevate.viewmodel.cvreview.CVReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVReviewResultScreen(
  navController: NavController,
  viewModel: CVReviewViewModel
) {
  val uiState by viewModel.uiState.collectAsState()
  val cvData = uiState.cvReviewData
  val fileDownloader = rememberFileDownloader()
  
  // Debug logs
  LaunchedEffect(uiState) {
    Log.d("CVReviewResultScreen", "UiState updated - hasData: ${cvData != null}")
    cvData?.let { data ->
      Log.d("CVReviewResultScreen", "CV Data - fileName: ${data.fileName}, careerField: ${data.careerField}")
      Log.d("CVReviewResultScreen", "Scores - relevancy: ${data.scores.relevancyRate}, overall: ${data.scores.overallScore}")
      Log.d("CVReviewResultScreen", "AI Analysis - summary: ${data.aiAnalysis.summary.take(50)}...")
    }
  }
  
  var relevancyRate by remember { mutableStateOf(0f) }
  var isFinished by remember { mutableStateOf(false) }
  
  val progressColor = MaterialTheme.colorScheme.primary
  val progressTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
  val animatedProgress = remember { Animatable(0f) }

  // Animate progress to actual relevancy rate from API
  LaunchedEffect(cvData) {
    if (cvData != null) {
      Log.d("CVReviewResultScreen", "Starting animation with relevancy rate: ${cvData.scores.relevancyRate}")
      val targetProgress = cvData.scores.relevancyRate / 100.0
      animatedProgress.animateTo(
        targetValue = targetProgress.toFloat(),
        animationSpec = tween(durationMillis = 2000, delayMillis = 200)
      ) {
        relevancyRate = this.value * 100
        if (relevancyRate.toInt() >= cvData.scores.relevancyRate.toInt()) {
          isFinished = true
          Log.d("CVReviewResultScreen", "Animation finished - isFinished: true")
        }
      }
    } else {
      Log.w("CVReviewResultScreen", "cvData is null, cannot start animation")
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("CV Review Result") },
        navigationIcon = {
          IconButton(onClick = { 
              // Balik ke list kalau ada, kalau enggak ke home
              val navigated = navController.popBackStack("cv_review_list", false)
              if (!navigated) navController.navigate("home") 
          }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          // Download button di top bar
          if (cvData != null) {
            IconButton(
              onClick = {
                fileDownloader.downloadFile(
                  url = cvData.fileUrl,
                  fileName = cvData.fileName
                )
              }
            ) {
              Icon(
                Icons.Filled.Download,
                contentDescription = "Download CV"
              )
            }
          }
        }
      )
    },
    content = { innerPadding ->
      LazyColumn(
        modifier = Modifier
          .padding(innerPadding)
          .padding(16.dp)
          .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        item {
          // Relevancy Rate Circular Progress
          Text(
            text = "Relevancy Rate",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
          )

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(180.dp)
              .padding(bottom = 24.dp)
          ) {
            CircularProgressIndicator(
              progress = animatedProgress.value,
              strokeWidth = 12.dp,
              color = progressColor,
              trackColor = progressTrackColor,
              modifier = Modifier.size(150.dp)
            )
            Text(
              text = "${relevancyRate.toInt()}%",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
              color = progressColor
            )
          }
        }

        if (cvData != null) {
          item {
            CVResultDisplay(
              cvData = cvData,
              onDownloadClick = {
                fileDownloader.downloadFile(
                  url = cvData.fileUrl,
                  fileName = cvData.fileName
                )
              }
            )
          }
        } else {
          item {
            Text(
              text = "Loading CV analysis...",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.padding(top = 16.dp)
            )
          }
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
          
          // Action buttons
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            // Download button
            if (cvData != null) {
              Button(
                onClick = {
                  fileDownloader.downloadFile(
                    url = cvData.fileUrl,
                    fileName = cvData.fileName
                  )
                },
                modifier = Modifier.weight(1f),
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
                Text("Download CV")
              }
            }
            
            // Back to Home button
            PrimaryButton(
              text = "Back to Home",
              onClick = { navController.navigate("home") },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  )
}

@Composable
fun CVResultDisplay(
  cvData: com.application.elevate.model.CVReviewData,
  onDownloadClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.Start,
    modifier = Modifier.fillMaxWidth()
  ) {
    // File Info Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Column(
        modifier = Modifier.padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "File: ${cvData.fileName}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Size: ${cvData.fileSize / 1024} KB",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
          }
          IconButton(onClick = onDownloadClick) {
            Icon(Icons.Filled.Download, contentDescription = "Download")
          }
        }
      }
    }
    
    // Career Field Info
    Text(
      text = "For Targeted Job",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
      text = cvData.careerField,
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(bottom = 16.dp)
    )

    // Overall Score Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Overall Score",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "${cvData.scores.overallScore.toInt()}%",
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    // Detailed Scores
    Text(
      text = "Detailed Scoring",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    val scoreMetrics = listOf(
      "Targeted Job Rate" to cvData.scores.targetedJobRate,
      "Relevant Skills" to cvData.scores.relevantSkill,
      "Work Experience" to cvData.scores.workExperience,
      "Consistency" to cvData.scores.consistency,
      "Writing Quality" to cvData.scores.writingQuality
    )

    scoreMetrics.forEach { (metric, score) ->
      ScoreCard(metric = metric, score = score)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // AI Analysis
    Text(
      text = "AI Analysis",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    ExpandableCard(
      title = "Summary",
      content = cvData.aiAnalysis.summary
    )

    ExpandableCard(
      title = "Career Field Fit",
      content = cvData.aiAnalysis.careerFieldFit
    )

    ExpandableCard(
      title = "Strengths",
      content = cvData.aiAnalysis.strengths.joinToString("\n• ", "• ")
    )

    ExpandableCard(
      title = "Areas for Improvement",
      content = cvData.aiAnalysis.weaknesses.joinToString("\n• ", "• ")
    )

    // Suggestions
    if (cvData.suggestions.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Suggestions",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      cvData.suggestions.forEachIndexed { index, suggestion ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Text(
            text = "${index + 1}. $suggestion",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    }
  }
}

@Composable
fun ScoreCard(metric: String, score: Double) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = metric,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f)
      )
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

@Composable
fun ExpandableCard(title: String, content: String) {
  var expanded by remember { mutableStateOf(false) }
  
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clip(RoundedCornerShape(8.dp)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = !expanded }
        .padding(12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
          imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
          contentDescription = "Expand/Collapse",
          tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }
      if (expanded) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
          text = content,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
      }
    }
  }
}