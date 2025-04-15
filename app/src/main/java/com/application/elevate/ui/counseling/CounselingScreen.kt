package com.application.elevate.ui.counseling

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.application.elevate.component.CategoryItem
import com.application.elevate.component.ConsultantCard
import com.application.elevate.component.SearchBar
import com.application.elevate.model.Consultant

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CounselingScreen(
    viewModel: CounselingViewModel,
    navController: NavController,
    onConsultantSelected: (Consultant) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState(initial = CounselingUiState())


    // Gunakan Scaffold agar top app bar & FAB/BottomBar bisa diatur
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Counseling")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // ----- Bagian atas: Search bar + sub-heading -----
            SearchBar(

                query = uiState.searchQuery,
                onQueryChange = { viewModel.search(it) },
                placeholderText = "Search Consultant"
            )
            Text(
                text = "Seek guide from the professionals\nFind a category that fits your situation",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // ----- Bagian Kategori -----
//            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
//                items(listOf("Design", "Development", "Finance", "Programming")) { cat ->
//                    CategoryItem(
//                        label = cat,
//                        modifier = Modifier.padding(end = 8.dp)
//                    ) { viewModel.selectCategory(cat) }
//                }
//            }
            CategoryScreen {

            }

            // ----- Bagian Recommendation -----
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    "Recommendation",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { /* On View All clicked */ }) {
                    Text("View All")
                }
            }

            // List consultant
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.consultants) { consultant ->
                    ConsultantCard(
                        consultant = consultant,
                        onClick = {
                            viewModel.selectConsultant(consultant)
                            // Panggil callback external jika ada
                            onConsultantSelected(consultant)
                        })

                }
            }
        }
    }


}