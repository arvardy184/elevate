package com.application.elevate.ui.assessment

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.application.elevate.component.RadioButtonGroup
import com.application.elevate.component.SearchableDropdown
import com.application.elevate.model.AssessmentStep
import com.application.elevate.model.QuestionType

@Composable
fun AssessmentStepPage(
    step: AssessmentStep,
    answer: String,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    isNextEnabled: Boolean,
    onSubmit: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val TAG = "AssessmentStepPage"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 110.dp, start = 24.dp, end = 24.dp, bottom = 64.dp)
            .verticalScroll(scrollState)
    ) {
        Text(step.title, style = MaterialTheme.typography.headlineMedium)
        Text(step.subtitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        when (step.type) {
            QuestionType.RADIO -> RadioButtonGroup(
                title = step.optionTitle,
                subTitle = step.optionSubtitle,
                options = step.options,
                selectedOption = answer,
                onOptionSelected = onOptionSelected
            )
            QuestionType.DROPDOWN -> key(step.key) {
                SearchableDropdown(
                    title = step.optionTitle,
                    subTitle = step.optionSubtitle,
                    options = step.options,
                    selectedOption = answer,
                    onOptionSelected = onOptionSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isFirst) {
                OutlinedButton(
                    onClick = onBack
                ) {
                    Text("Back")
                }
            }

            Button(
                onClick = {
                    if (isLast) {
                        Log.d(TAG, "Last step, submitting assessment")
                        onSubmit()
                    }
                    onNext()
                },
                enabled = isNextEnabled
            ) {
                Text(if (isLast) "Selesai" else "Next")
            }
        }
    }
}
