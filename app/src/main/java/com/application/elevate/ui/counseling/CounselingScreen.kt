package com.application.elevate.ui.counseling

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.component.CategoryCounselingItem
import com.application.elevate.component.CategoryItem
import com.application.elevate.component.ConsultantCard
import com.application.elevate.component.SearchBar
import com.application.elevate.data.dummy.ProfileDummyData.categoriesCounseling
import com.application.elevate.data.dummy.ProfileDummyData.consultants
import com.application.elevate.model.Consultant
import com.application.elevate.model.CounselingCategory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.elevate.component.SectionHeader


@Composable
fun CounselingScreen(
    navController: NavController,
    viewModel: CounselingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp),
        ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Counseling",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(21.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {  } // Pindah ke luar
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = {Text(
                    "Search here...",
                    color = Color.Gray // Custom color for placeholder text
                )},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray // Custom color for icon
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.95f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                    disabledContainerColor = Color.White.copy(alpha = 0.85f) // Keep same color when disabled
                )
            )
        }
        Spacer(modifier = Modifier.height(21.dp))


        Text("Seek guide from the professionals", style = MaterialTheme.typography.titleMedium)
        Text("Find a category that fits your situation", style = MaterialTheme.typography.bodySmall)

        LazyRow(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.categories) { category ->
                val isSelected = viewModel.selectedCategory.value?.id == category.id
                CategoryCounselingItem(
                    category = category,
                    isSelected = isSelected,
                    onClick = { viewModel.onCategorySelected(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Recommendation", onViewAllClick = {viewModel.showAllConsultants()}

        )
        val displayedConsultants = if (viewModel.showAll.value) {
            uiState.consultants
        } else {
            uiState.consultants.take(3)
        }


        LazyColumn {
            items(displayedConsultants) { consultant ->
                ConsultantCard(consultant)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewConselingScreen() {
    val navController = rememberNavController()
    CounselingScreen(navController = navController)
}
