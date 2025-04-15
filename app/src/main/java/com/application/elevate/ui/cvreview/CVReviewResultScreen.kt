package com.application.elevate.ui.cvreview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.application.elevate.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVReviewResultScreen(navController: NavController) {
    var relevancyRate by remember { mutableStateOf(0f) } // Start at 0% for progress
    var isFinished by remember { mutableStateOf(false) } // Track if progress is complete
    val targetJob = "Product Manager"
    val metrics = remember {
        listOf(
            "Overall Impression" to "Your CV makes a solid first impression and is well-organized.",
            "Relevant Skills" to "Demonstrates strong alignment with the required skills for a Product Manager role.",
            "Work Experience" to "Your experience showcases relevant projects and responsibilities.",
            "Consistent & Error Writing" to "The writing is consistent, with minor suggestions for improvement."
        )
    }

    val progressColor = MaterialTheme.colorScheme.primary
    val progressTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    val animatedProgress = remember { Animatable(0f) }

    // Simulate circular progress reaching 100% with animation
    LaunchedEffect(true) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, delayMillis = 200) // Lebih halus dan ada delay
        ) {
            relevancyRate = this.value * 100
            if (relevancyRate.toInt() == 100) {
                isFinished = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.cv_review_result_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Relevancy Rate Circular Progress
                Text(
                    text = stringResource(id = R.string.relevancy_rate),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(180.dp) // Lebih besar sedikit
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
                        text = "${relevancyRate.toInt()}%", // Display the percentage
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = progressColor
                    )
                }

                if (isFinished) {
                    ResultDisplay(targetJob = targetJob, metrics = metrics)
                } else {
                    Text(
                        text = stringResource(id = R.string.analyzing_cv),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                PrimaryButton(text = "Back to Home") {
                    // Navigate to home screen when clicked
                    navController.navigate("home_screen")
                }
            }
        }
    )
}

@Composable
fun ResultDisplay(targetJob: String, metrics: List<Pair<String, String>>) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.for_targeted_job),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = targetJob,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.scoring_metrics),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        metrics.forEach { (metric, detail) ->
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
                            text = metric,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = stringResource(id = R.string.expand_collapse),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (expanded) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}