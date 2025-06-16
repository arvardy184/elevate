@file:OptIn(ExperimentalMaterial3Api::class)

package com.application.elevate.ui.jobmatching

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.application.elevate.model.jobmatching.JobMatch
import com.application.elevate.model.jobmatching.AIJobAnalysis
import com.application.elevate.ui.component.JobMatchingLoadingScreen
import com.application.elevate.viewmodel.jobmatching.JobMatchingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobMatchingResultScreen(
    navController: NavController,
    viewModel: JobMatchingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load history data when screen loads
    LaunchedEffect(Unit) {
        viewModel.getJobMatchingHistory()
    }

    // Show loading screen while fetching history
    if (uiState.isLoadingHistory) {
        JobMatchingLoadingScreen()
        return
    }

    // Get the latest job matching data from history
    val historyData = uiState.historyResult?.data?.firstOrNull()

    // Error handling for missing or corrupted data
    if (historyData == null) {
        ErrorResultScreen(
            title = "Data Tidak Ditemukan",
            message = "Hasil job matching tidak ditemukan. Silakan coba lagi.",
            onRetry = { navController.popBackStack() }
        )
        return
    }

    // Validate data integrity - take only top 3 matches
    val topMatches = historyData.matches.take(3)
    if (topMatches.isEmpty()) {
        ErrorResultScreen(
            title = "Tidak Ada Job Match",
            message = "Maaf, tidak ada job yang cocok dengan CV dan dream job kamu. Coba dengan posisi lain atau update CV kamu.",
            onRetry = { 
                viewModel.resetState()
                navController.popBackStack() 
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Job Matches Found",
                    fontWeight = FontWeight.Bold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Summary
            if (topMatches.isNotEmpty() && historyData.dreamJob.isNotBlank()) {
                SummaryCard(
                    totalMatches = topMatches.size,
                    dreamJob = historyData.dreamJob
                )
            } else {
                ErrorCard(message = "Error displaying summary: Invalid data")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Job Matches List
            Text(
                text = "Top Job Matches",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (topMatches.isNotEmpty()) {
                topMatches.forEach { jobMatch ->
                    JobMatchCard(jobMatch = jobMatch)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                ErrorCard(message = "No job matches available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Analysis
            if (historyData.aiAnalysis.summary.isNotBlank()) {
                AIAnalysisCard(aiAnalysis = historyData.aiAnalysis)
            } else {
                ErrorCard(message = "AI analysis not available")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { 
                        viewModel.resetState()
                        navController.popBackStack() 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Search")
                }

                Button(
                    onClick = { 
                        // Navigate to full job list or save results
                        // For now, show a snackbar
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Results")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ErrorResultScreen(
    title: String,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { 
                Text(
                    text = "Job Matching",
                    fontWeight = FontWeight.Bold
                ) 
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Coba Lagi")
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SummaryCard(
    totalMatches: Int,
    dreamJob: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Analysis Complete!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Found $totalMatches job matches for $dreamJob position",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun JobMatchCard(jobMatch: JobMatch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with match score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = jobMatch.title.takeIf { it.isNotBlank() } ?: "Job Title Not Available",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = jobMatch.company.takeIf { it.isNotBlank() } ?: "Company Not Available",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Match Score Circle
                AnimatedMatchScore(score = jobMatch.matchScore.coerceIn(0, 100))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreItem(
                    label = "Skills",
                    score = jobMatch.skillMatch.coerceIn(0, 100),
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.weight(1f)
                )
                ScoreItem(
                    label = "Experience",
                    score = jobMatch.experienceMatch.coerceIn(0, 100),
                    icon = Icons.Default.WorkHistory,
                    modifier = Modifier.weight(1f)
                )
                ScoreItem(
                    label = "Location",
                    score = jobMatch.locationMatch.coerceIn(0, 100),
                    icon = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Strengths
            if (jobMatch.strengths.isNotEmpty()) {
                Text(
                    text = "Strengths:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                jobMatch.strengths.take(2).forEach { strength ->
                    if (strength.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strength,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Missing Skills
            if (jobMatch.missingSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Areas to improve:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
                jobMatch.missingSkills.take(2).forEach { skill ->
                    if (skill.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = skill,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedMatchScore(score: Int) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1000),
        label = "score_animation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(
                when {
                    score >= 80 -> MaterialTheme.colorScheme.primary
                    score >= 60 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                }
            )
    ) {
        Text(
            text = "$animatedScore%",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ScoreItem(
    label: String,
    score: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$score%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AIAnalysisCard(aiAnalysis: AIJobAnalysis) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = "AI Analysis",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Career Analysis",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = aiAnalysis.summary.takeIf { it.isNotBlank() } ?: "No summary available",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Recommendations
                if (aiAnalysis.recommendations.isNotEmpty()) {
                    AnalysisSection(
                        title = "Recommendations",
                        items = aiAnalysis.recommendations.filter { it.isNotBlank() },
                        icon = Icons.Default.Lightbulb
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Skill Gaps
                if (aiAnalysis.skillGaps.isNotEmpty()) {
                    AnalysisSection(
                        title = "Skill Gaps to Address",
                        items = aiAnalysis.skillGaps.filter { it.isNotBlank() },
                        icon = Icons.Default.TrendingUp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Career Path
                if (aiAnalysis.careerPath.isNotBlank()) {
                    Text(
                        text = "Career Path Advice:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = aiAnalysis.careerPath,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisSection(
    title: String,
    items: List<String>,
    icon: ImageVector
) {
    if (items.isEmpty()) return
    
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    items.forEach { item ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
} 