package com.application.elevate.ui.cvreview

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVReviewScreen(
  navController: NavController, 
  viewModel: CVReviewViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()
  
  var selectedFormat by remember { mutableStateOf("") }
  var careerField by remember { mutableStateOf("") }
  var selectedFile by remember { mutableStateOf<File?>(null) }
  
  // File picker launcher
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    uri?.let {
      val file = FilePicker.getFileFromUri(context, it, "cv.pdf")
      selectedFile = file
    }
  }
  
  // Navigate to result when upload success
  LaunchedEffect(uiState.isUploadSuccess, uiState.cvReviewData) {
    Log.d("CVReviewScreen", "LaunchedEffect triggered - isUploadSuccess: ${uiState.isUploadSuccess}, hasData: ${uiState.cvReviewData != null}")
    
    if (uiState.isUploadSuccess && uiState.cvReviewData != null) {
      Log.d("CVReviewScreen", "Conditions met, navigating to cv_result_review...")
      try {
        navController.navigate("cv_result_review") {
          popUpTo("cv_review") { inclusive = false }
        }
        Log.d("CVReviewScreen", "Navigation successful")
      } catch (e: Exception) {
        Log.e("CVReviewScreen", "Navigation failed", e)
      }
    }
  }
  
  // Show error snackbar
  uiState.error?.let { error ->
    LaunchedEffect(error) {
      // You can show snackbar here if needed
      Log.e("CVReviewScreen", "Error: $error")
    }
  }

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
        Button(
          onClick = { filePickerLauncher.launch("application/pdf") },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = if (selectedFile != null) "File Selected: ${selectedFile!!.name}" else "Upload CV (PDF only)"
          )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Career Field Dropdown
        CareerFieldDropdown(
          selectedCareerField = careerField,
          onCareerFieldSelected = { careerField = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error message
        uiState.error?.let { error ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
          ) {
            Text(
              text = error,
              modifier = Modifier.padding(16.dp),
              color = MaterialTheme.colorScheme.onErrorContainer
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
        }

        // Primary Button for submitting CV
        PrimaryButton(
          text = if (uiState.isLoading) "Uploading..." else "Get My CV Reviewed",
          enabled = !uiState.isLoading && selectedFile != null && careerField.isNotEmpty(),
          isLoading = uiState.isLoading,
          onClick = {
            selectedFile?.let { file ->
              Log.d("CVReviewScreen", "File selected: ${file.name}, Career field: $careerField")
              // Use the simple uploadCV method that gets token automatically
              viewModel.uploadCV(file, careerField)
            }
          }
        )
      }
    }
  )
}

@Composable
fun PrimaryButton(
  text: String, 
  enabled: Boolean, 
  isLoading: Boolean, 
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    enabled = enabled
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = MaterialTheme.colorScheme.onPrimary
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(text)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerFieldDropdown(
  selectedCareerField: String,
  onCareerFieldSelected: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  val careerFields = listOf(
    "Software Engineer",
    "Data Scientist", 
    "Product Manager",
    "UI/UX Designer",
    "Marketing",
    "Finance",
    "Other"
  )

  Column {
    Text(
      text = "Target Career Field",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    
    ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = { expanded = !expanded }
    ) {
      OutlinedTextField(
        value = selectedCareerField,
        onValueChange = { },
        readOnly = true,
        placeholder = { Text("Select career field") },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        modifier = Modifier
          .menuAnchor()
          .fillMaxWidth()
      )
      
      ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
      ) {
        careerFields.forEach { field ->
          DropdownMenuItem(
            text = { Text(field) },
            onClick = {
              onCareerFieldSelected(field)
              expanded = false
            }
          )
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewCVReviewScreen() {
  // Preview implementation - can't use hiltViewModel in preview
}
