package com.application.elevate.ui.cvreview

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun rememberFilePicker(
  onFileSelected: (File?) -> Unit
): () -> Unit {
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    onFileSelected(uri?.let { /* Convert URI to File if needed */ null })
  }
  
  return remember {
    {
      launcher.launch("application/pdf")
    }
  }
}

// Utility function untuk convert URI ke File
// fun uriToFile(context: Context, uri: Uri, fileName: String = "cv.pdf"): File? {
//   return try {
//     val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//     val tempFile = File(context.cacheDir, fileName)
    
//     inputStream?.use { input ->
//       FileOutputStream(tempFile).use { output ->
//         input.copyTo(output)
//       }
//     }
    
//     tempFile
//   } catch (e: Exception) {
//     e.printStackTrace()
//     null
//   }
// } 