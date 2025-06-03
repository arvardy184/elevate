package com.application.elevate.ui.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.application.elevate.R
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.User
import com.application.elevate.ui.theme.Purple5
import com.application.elevate.ui.theme.Purple6
import com.application.elevate.ui.theme.ReplyTheme
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.widget.Toast

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Load user data when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    // Update selectedImageUri when user's photoUrl changes
    LaunchedEffect(uiState.user.photoUrl) {
        if (uiState.user.photoUrl?.isNotEmpty() == true) {
            try {
                selectedImageUri = Uri.parse(uiState.user.photoUrl)
            } catch (e: Exception) {
                Log.e("EditProfileScreen", "Error parsing photo URL: ${e.message}")
            }
        }
    }

    // Galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            viewModel.setProfileImageUri(it)
        }
    }

    // Kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            selectedImageUri = cameraImageUri
            viewModel.setProfileImageUri(cameraImageUri!!)
        }
    }

    // Menyiapkan file URI untuk kamera
    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            image
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        EditProfileContent(
            user = uiState.user,
            selectedImageUri = selectedImageUri,
            onNavigateBack = { navController.popBackStack() },
            onProfilePictureClick = { viewModel.showChangeProfilePicture() },
            onSaveChanges = { updatedUser ->
                viewModel.updateUser(updatedUser)
                navController.popBackStack()
            }
        )
    }

    if (uiState.isChangingProfilePicture) {
        ChangeProfileBottomSheet(
            onDismiss = { viewModel.hideChangeProfilePicture() },
            onChooseFromGallery = {
                galleryLauncher.launch("image/*")
            },
            onTakePicture = {
                val uri = createImageUri(context)
                cameraImageUri = uri
                cameraLauncher.launch(uri)
            }
        )
    }

    // Show error message if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            val errorMessage = when {
                error.contains("Job was cancelled") -> "Koneksi terputus. Silakan coba lagi."
                error.contains("Socket closed") -> "Koneksi terputus. Silakan coba lagi."
                else -> "Terjadi kesalahan: $error"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    user: User,
    selectedImageUri: Uri?,
    onNavigateBack: () -> Unit,
    onProfilePictureClick: () -> Unit,
    onSaveChanges: (User) -> Unit
) {
    var firstName by remember { mutableStateOf(user.firstName ?: "") }
    var lastName by remember { mutableStateOf(user.lastName ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var address by remember { mutableStateOf(user.address ?: "") }
    var phoneNumber by remember { mutableStateOf(user.phoneNumber ?: "") }
    var gender by remember { mutableStateOf(user.gender ?: "") }
    var birthDate by remember { mutableStateOf(user.birthDate ?: "") }

    LaunchedEffect(user) {
        firstName = user.firstName ?: ""
        lastName = user.lastName ?: ""
        email = user.email ?: ""
        address = user.address ?: ""
        phoneNumber = user.phoneNumber ?: ""
        gender = user.gender ?: ""
        birthDate = user.birthDate ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Custom Header with Profile Picture
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Purple Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Purple6)
                    .align(Alignment.TopCenter)
            ) {
                // Back Button and Title in a Box layout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Back Button (Left aligned)
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Title (Center aligned)
                    Text(
                        text = "Edit Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }
            }

            // Profile Picture (positioned to overlap the purple background)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20).dp)
                    .zIndex(1f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onProfilePictureClick),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = selectedImageUri),
                        contentDescription = "Selected Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onProfilePictureClick)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onProfilePictureClick)
                    )
                }
            }

            // "Change Profile" text
            Text(
                text = "Change Profile",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .clickable(onClick = onProfilePictureClick)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "First Name",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                placeholder = { Text(text = user.firstName ?: "Enter first name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Last Name",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                placeholder = { Text(text = user.lastName ?: "Enter last name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                placeholder = { Text(text = user.email ?: "Enter email") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Address",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "View on map"
                    )
                },
                placeholder = { Text(text = user.address ?: "Enter address") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Phone Number",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                placeholder = { Text(text = user.phoneNumber ?: "Enter phone number") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gender",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select gender"
                    )
                },
                placeholder = { Text(text = user.gender ?: "Select gender") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Birth Date",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select date"
                    )
                },
                placeholder = { Text(text = user.birthDate ?: "Select birth date") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val updatedUser = user.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        address = address,
                        phoneNumber = phoneNumber,
                        gender = gender,
                        birthDate = birthDate
                    )
                    onSaveChanges(updatedUser)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

//@Preview
//@Composable
//fun EditProfileScreenPreview() {
//    ReplyTheme {
//        EditProfileContent(
//            user = ProfileDummyData.currentUser,
//            onNavigateBack = {},
//            selectedImageUri: {},
//        onProfilePictureClick = {},
//            onSaveChanges = {}
//        )
//    }
//}
//@Preview
//@Composable
//fun EditProfileScreenPreviewFull() {
//    ReplyTheme {
//        val navController = rememberNavController()
//        val viewModel = remember { ProfileViewModel.createPreviewViewModel() }
//
//        EditProfileScreen(
//            navController = navController,
//            viewModel = viewModel
//        )
//    }
//}