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

// Utility object untuk file operations
object FilePicker {
    
    // Function untuk convert URI ke File
    fun getFileFromUri(context: Context, uri: Uri, fileName: String = "cv.pdf"): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Function untuk cek apakah file valid PDF
    fun isValidPdfFile(file: File?): Boolean {
        if (file == null || !file.exists()) return false
        
        return try {
            val fileName = file.name.lowercase()
            val extension = fileName.substringAfterLast('.', "")
            
            // Cek extension
            if (extension != "pdf") return false
            
            // Cek file size (max 10MB)
            val maxSize = 10 * 1024 * 1024 // 10MB
            if (file.length() > maxSize) return false
            
            // Cek apakah file readable
            file.canRead()
        } catch (e: Exception) {
            false
        }
    }
    
    // Function untuk format file size
    fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024} KB"
            else -> "${"%.1f".format(sizeInBytes / (1024.0 * 1024.0))} MB"
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