package com.application.elevate.ui.assessment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.elevate.R
import com.application.elevate.utils.NetworkUtils
import com.application.elevate.viewmodel.assessment.AssessmentCompletedViewModel

@Composable
fun AssessmentCompletedScreen(
    viewModel: AssessmentCompletedViewModel = hiltViewModel(),
    onExploreClicked: () -> Unit
) {
    val backgroundColor = Color(0xFF665492)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else if (uiState.error != null) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (!NetworkUtils.isOnline(context)) {
                        "Tidak ada koneksi internet. Silakan cek koneksi Anda."
                    } else {
                        uiState.error ?: "Terjadi kesalahan"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        if (NetworkUtils.isOnline(context)) {
                            viewModel.fetchAssessmentHistory()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Coba Lagi",
                        color = backgroundColor
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_completed),
                    contentDescription = "Assessment Completed",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Assessment Completed!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Menampilkan data assessment
                uiState.assessmentHistory?.let { history ->
                    AssessmentResultCard(
                        modifier = Modifier.fillMaxWidth(),
                        history = history
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onExploreClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Explore Opportunities",
                        color = backgroundColor,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun AssessmentResultCard(
    modifier: Modifier = Modifier,
    history: com.application.elevate.model.AssessmentHistory
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AssessmentResultItem("Status", history.studentStatus)
            AssessmentResultItem("Jurusan", history.majorStudy)
            AssessmentResultItem("Semester", history.currentSemester)
            AssessmentResultItem("Bidang Saat Ini", history.currentField)
            AssessmentResultItem("Bidang Diminati", history.interestedField)
            AssessmentResultItem("Pekerjaan Impian", history.dreamJob)
            AssessmentResultItem("Tujuan Utama", history.mainGoal)
        }
    }
}

@Composable
private fun AssessmentResultItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF665492),
                fontWeight = FontWeight.Bold
            )
        )
    }
}
