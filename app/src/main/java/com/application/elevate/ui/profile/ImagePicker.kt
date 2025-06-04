package com.application.elevate.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberImagePicker(
    context: Context,
    onImagePicked: (Uri) -> Unit,
    onCameraImageCaptured: (Uri) -> Unit
): ImagePickerLauncher {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onImagePicked(it) }
        }
    )

    val tempImageUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri.value?.let { onCameraImageCaptured(it) }
        }
    }

    return remember {
        ImagePickerLauncher(
            pickFromGallery = {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            takePhoto = {
                val photoFile = File.createTempFile("profile_", ".jpg", context.cacheDir).apply {
                    createNewFile()
                    deleteOnExit()
                }
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    photoFile
                )
                tempImageUri.value = uri
                cameraLauncher.launch(uri)
            }
        )
    }
}

class ImagePickerLauncher(
    val pickFromGallery: () -> Unit,
    val takePhoto: () -> Unit
)