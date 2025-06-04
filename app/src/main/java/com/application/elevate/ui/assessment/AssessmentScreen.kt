package com.application.elevate.ui.assessment

import android.util.Log
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.elevate.data.dummy.ProfileDummyData.assessmentDummyData
import com.application.elevate.viewmodel.assessment.AssessmentViewModel

@Composable
fun AssessmentScreen(
    viewModel: AssessmentViewModel = hiltViewModel(),
    navController: NavController
) {
    val TAG = "AssessmentScreen"
    val uiState by viewModel.uiState.collectAsState()
    var stepIndex by remember { mutableStateOf(0) }

    val steps = remember { assessmentDummyData }
    val currentStep = steps[stepIndex]

    val currentAnswer = remember(uiState, currentStep.key) {
        viewModel.getAnswerForStep(currentStep.key)
    }

    val isAnswered = !currentAnswer.isNullOrEmpty()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Log.d(TAG, "Assessment submitted successfully, navigating to assessment_done")
            navController.navigate("assessment_done")
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Log.e(TAG, "Error during assessment: ${uiState.error}")
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
                Log.d(TAG, "Last step reached, submitting assessment")
                viewModel.submitAssessment()
            }
        },
        onBack = { if (stepIndex > 0) stepIndex-- },
        isFirst = stepIndex == 0,
        isLast = stepIndex == steps.lastIndex,
        isNextEnabled = isAnswered,
        onSubmit = {
            Log.d(TAG, "Submit button clicked, submitting assessment")
            viewModel.submitAssessment()
        }
    )
}