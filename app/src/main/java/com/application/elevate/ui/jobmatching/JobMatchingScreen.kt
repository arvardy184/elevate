package com.application.elevate.ui.jobmatching

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.application.elevate.ui.cvreview.PrimaryButton
import com.application.elevate.ui.cvreview.FilePicker
import com.application.elevate.viewmodel.jobmatching.JobMatchingViewModel
import com.application.elevate.viewmodel.jobmatching.JobMatchingError
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobMatchingScreen(
    navController: NavController,
    viewModel: JobMatchingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var dreamJob by remember { mutableStateOf("") }
    
    // Navigate to result screen when upload is successful
    LaunchedEffect(uiState.isUploadSuccessful) {
        if (uiState.isUploadSuccessful && uiState.jobMatchingResult != null) {
            navController.navigate("job_matching_result") {
                popUpTo("job_matching") { inclusive = false }
            }
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val file = FilePicker.getFileFromUri(context, it)
                if (file != null && FilePicker.isValidPdfFile(file)) {
                    selectedFile = file
                    // Clear previous file validation errors when new file is selected
                    if (uiState.validationErrors.containsKey("cvFile")) {
                        viewModel.clearError()
                    }
                } else {
                    // Show error for invalid file
                    viewModel.clearError() // Clear first, then set new error
                    // You might want to add a method to set validation error in viewmodel
                }
            } catch (e: Exception) {
                // Handle file selection error
                e.printStackTrace()
            }
        }
    }

    // Enhanced Error Dialog with different types
    if (uiState.errorMessage != null) {
        when (uiState.errorType) {
            JobMatchingError.NetworkError -> {
                NetworkErrorDialog(
                    onDismiss = { viewModel.clearError() },
                    onRetry = if (uiState.canRetry && selectedFile != null) {
                        { viewModel.uploadAndMatchJobs(selectedFile!!, dreamJob) }
                    } else null
                )
            }
            JobMatchingError.AuthenticationError -> {
                AuthErrorDialog(
                    onDismiss = { viewModel.clearError() },
                    onLoginAgain = { 
                        viewModel.clearError()
                        navController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            else -> {
                StandardErrorDialog(
                    title = when (uiState.errorType) {
                        JobMatchingError.FileValidationError -> "File Error"
                        JobMatchingError.ServerError -> "Server Error"
                        else -> "Error"
                    },
                    message = uiState.errorMessage!!,
                    onDismiss = { viewModel.clearError() },
                    onRetry = if (uiState.canRetry && selectedFile != null) {
                        { viewModel.uploadAndMatchJobs(selectedFile!!, dreamJob) }
                    } else null
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Job & Skill Matching",
                    fontWeight = FontWeight.Bold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header Icon
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = "Job Matching",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title and Description
            Text(
                text = "Find Your Perfect Job Match",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Upload your CV and tell us your dream job. Our AI will find the best job matches for you based on your skills and experience.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Dream Job Input
            OutlinedTextField(
                value = dreamJob,
                onValueChange = { 
                    dreamJob = it
                    // Clear validation error when user types
                    if (uiState.validationErrors.containsKey("dreamJob")) {
                        viewModel.clearError()
                    }
                },
                label = { Text("Dream Job Position") },
                placeholder = { Text("e.g., Software Engineer, Data Scientist") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Work, contentDescription = null)
                },
                shape = RoundedCornerShape(12.dp),
                isError = uiState.validationErrors.containsKey("dreamJob"),
                supportingText = {
                    uiState.validationErrors["dreamJob"]?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // File Upload Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.validationErrors.containsKey("cvFile")) {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                ),
                border = if (uiState.validationErrors.containsKey("cvFile")) {
                    CardDefaults.outlinedCardBorder().copy(
                        width = 1.dp,
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                    )
                } else null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (uiState.validationErrors.containsKey("cvFile")) {
                            Icons.Default.Error
                        } else {
                            Icons.Default.CloudUpload
                        },
                        contentDescription = "Upload CV",
                        modifier = Modifier.size(48.dp),
                        tint = if (uiState.validationErrors.containsKey("cvFile")) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Upload Your CV",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (selectedFile != null) {
                            "Selected: ${selectedFile!!.name}"
                        } else {
                            "Select a PDF file to continue"
                        },
                        fontSize = 14.sp,
                        color = if (selectedFile != null) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center
                    )

                    // Show validation error for file
                    uiState.validationErrors["cvFile"]?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose File")
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Upload Button
            PrimaryButton(
                text = "Find Job Matches",
                onClick = {
                    if (selectedFile != null && dreamJob.isNotBlank()) {
                        viewModel.uploadAndMatchJobs(selectedFile!!, dreamJob)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedFile != null && dreamJob.isNotBlank() && !uiState.isLoading,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info Text
            Text(
                text = "• Supported format: PDF only\n• Maximum file size: 10MB\n• Processing time: 30-60 seconds",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun NetworkErrorDialog(
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Koneksi Bermasalah") },
        text = { 
            Text("Internet kamu lagi bermasalah nih. Cek koneksi dan coba lagi ya!") 
        },
        confirmButton = {
            if (onRetry != null) {
                Button(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Coba Lagi")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun AuthErrorDialog(
    onDismiss: () -> Unit,
    onLoginAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Sesi Expired") },
        text = { 
            Text("Sesi login kamu udah habis. Login ulang dulu ya!") 
        },
        confirmButton = {
            Button(onClick = onLoginAgain) {
                Text("Login Ulang")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Nanti")
            }
        }
    )
}

@Composable
private fun StandardErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            if (onRetry != null) {
                Button(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Coba Lagi")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
} 