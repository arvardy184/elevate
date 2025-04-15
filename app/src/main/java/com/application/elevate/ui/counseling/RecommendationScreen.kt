package com.application.elevate.ui.counseling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.elevate.component.ConsultantCard
import com.application.elevate.data.CounselingData
import com.application.elevate.model.Consultant
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun RecommendationScreen(
    uiState: CounselingUiState,
    onConsultantClick: (Consultant) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Recommendation", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onViewAll) { Text("View All") }
        }
        LazyColumn {
            items(uiState.consultants) { c ->
                ConsultantCard(consultant = c, onClick = { onConsultantClick(c) })
            }
        }
    }
}

@Preview
@Composable
fun PreviewRecommendation() {
    val dummy = CounselingData.getConsultants()
    ReplyTheme { RecommendationScreen(
        uiState = CounselingUiState(consultants = dummy),
        onConsultantClick = {}, onViewAll = {}
    ) }
}