package com.application.elevate.ui.cvreview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.application.elevate.ui.cvreview.CVFormatSelector
import com.application.elevate.ui.cvreview.FileUploadButton
import com.application.elevate.ui.cvreview.PrimaryButton
import com.application.elevate.ui.theme.Purple5
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVReviewScreen(navController: NavController, viewModel: CVReviewViewModel = CVReviewViewModel()) {
    var selectedFormat by remember { mutableStateOf("") }
    var careerField by remember { mutableStateOf("") }
    val careerFields = listOf("Engineering", "Marketing", "Design", "Finance") // Daftar bidang karir

    var expanded by remember { mutableStateOf(false) } // State untuk dropdown

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CV Review") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Craft a CV That Reflects Your Dream Career",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Choose Your CV Format
                Text(
                    text = "Choose Your CV Format",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // ATS Format
                    CVFormatBox(
                        label = "ATS",
                        imageRes = Icons.Filled.Description,
                        isSelected = selectedFormat == "ATS",
                        onSelect = { selectedFormat = "ATS" }
                    )

                    // Personal Design Format
                    CVFormatBox(
                        label = "Personal Design",
                        imageRes = Icons.Filled.Description,
                        isSelected = selectedFormat == "Personal Design",
                        onSelect = { selectedFormat = "Personal Design" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Upload CV Button
                FileUploadButton(
                    onFileUploaded = { /* Logic to upload file */ }
                )
                Spacer(modifier = Modifier.height(16.dp))


                CareerFieldDropdown()
//                // Dropdown untuk memilih bidang karir
//                OutlinedTextField(
//                    value = careerField,
//                    onValueChange = { careerField = it },
//                    label = { Text("Pick Your Career Field") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { expanded = true }, // Mengaktifkan dropdown saat diklik
//                    trailingIcon = {
//                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Icon")
//                    }
//                )
//
//                // Dropdown Menu
//                DropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    careerFields.forEach { field ->
//                        DropdownMenuItem(text = { /*TODO*/ }, onClick = { /*TODO*/ })
////                        DropdownMenuItem(onClick = {
////                            careerField = field
////                            expanded = false
////                        }) {
////                            Text(text = field)
////                        }
//                    }
//                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Button for submitting CV
                PrimaryButton(
                    text = "Get My CV Reviewed",
                    onClick = {
                        navController.navigate("cv_result_review")
//                        viewModel.submitCV(careerField, selectedFormat)
                    }
                )
            }
        }
    )
}



//@Composable
//fun FileUploadButton(onFileUploaded: () -> Unit) {
//    Button(onClick = { onFileUploaded() }, modifier = Modifier.fillMaxWidth()) {
//        Text("Upload CV")
//    }
//}
//
//@Composable
//fun PrimaryButton(text: String, onClick: () -> Unit) {
//    Button(onClick = { onClick() }, modifier = Modifier.fillMaxWidth()) {
//        Text(text)
//    }
//}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerFieldDropdown() {
    val careerFields = remember { listOf("Engineering", "Marketing", "Design", "Finance") }
    var expanded by remember { mutableStateOf(false) }
    var careerField by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = careerField,
            onValueChange = { careerField = it },
            readOnly = true,
            label = { Text("Pick Your Career Field") },
            modifier = Modifier
                .menuAnchor(), // Penting untuk menghubungkan TextField dengan DropdownMenu
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            careerFields.forEach { field ->
                DropdownMenuItem(
                    text = { Text(text = field) },
                    onClick = {
                        careerField = field
                        expanded = false
                    }
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewCVReviewScreen() {
//    CVReviewScreen(viewModel = CVReviewViewModel())
}
