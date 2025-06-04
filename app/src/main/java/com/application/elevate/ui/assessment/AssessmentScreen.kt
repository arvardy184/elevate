package com.application.elevate.ui.assessment

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.elevate.data.dummy.ProfileDummyData.assessmentDummyData
import com.application.elevate.utils.NetworkUtils
import com.application.elevate.viewmodel.assessment.AssessmentViewModel

@Composable
fun AssessmentScreen(
    viewModel: AssessmentViewModel = hiltViewModel(),
    navController: NavController
) {
    val TAG = "AssessmentScreen"
    val uiState by viewModel.uiState.collectAsState()
    var stepIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val steps = remember { assessmentDummyData }
    
    // Check if steps is empty
    if (steps.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tidak ada data assessment yang tersedia",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    // Show loading state
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Show error state
    if (uiState.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (!NetworkUtils.isOnline(context)) {
                        "Tidak ada koneksi internet. Silakan cek koneksi Anda."
                    } else {
                        uiState.error ?: "Terjadi kesalahan"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        if (NetworkUtils.isOnline(context)) {
                            viewModel.clearError()
                        }
                    }
                ) {
                    Text("Coba Lagi")
                }
            }
        }
        return
    }

    val currentStep = steps[stepIndex]
    val currentAnswer = remember(uiState, currentStep.key) {
        viewModel.getAnswerForStep(currentStep.key)
    }
    val isAnswered = !currentAnswer.isNullOrEmpty()

    //success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Log.d(TAG, "Assessment submitted successfully, navigating to assessment_done")
            navController.navigate("assessment_done")
        }
    }

    AssessmentStepPage(
        step = currentStep,
        answer = currentAnswer,
        onOptionSelected = { viewModel.updateAnswerForStep(currentStep.key, it) },
        onNext = {
            if (stepIndex < steps.lastIndex) {
                stepIndex++
            } else {
                if (NetworkUtils.isOnline(context)) {
                    Log.d(TAG, "Last step reached, submitting assessment")
                    viewModel.submitAssessment()
                } else {
                    viewModel.setError("Tidak ada koneksi internet. Silakan cek koneksi Anda.")
                }
            }
        },
        onBack = { if (stepIndex > 0) stepIndex-- },
        isFirst = stepIndex == 0,
        isLast = stepIndex == steps.lastIndex,
        isNextEnabled = isAnswered,
        onSubmit = {
            if (NetworkUtils.isOnline(context)) {
                Log.d(TAG, "Submit button clicked, submitting assessment")
                viewModel.submitAssessment()
            } else {
                viewModel.setError("Tidak ada koneksi internet. Silakan cek koneksi Anda.")
            }
        }
    )
}