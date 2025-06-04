package com.application.elevate.ui.cvreview

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileDownloader(private val context: Context) {
  fun downloadFile(url: String, fileName: String) {
    val request = DownloadManager.Request(Uri.parse(url))
      .setTitle("Downloading $fileName")
      .setDescription("CV Review file download")
      .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
      .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
      .setAllowedOverMetered(true)
      .setAllowedOverRoaming(true)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
  }
}

@Composable
fun rememberFileDownloader(): FileDownloader {
  val context = LocalContext.current
  return remember { FileDownloader(context) }
}

// Utility function untuk convert URI to File
fun uriToFile(context: Context, uri: Uri, fileName: String): File {
  val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
  val file = File(context.cacheDir, fileName)
  val outputStream = FileOutputStream(file)

  inputStream?.use { input ->
    outputStream.use { output ->
      input.copyTo(output)
    }
  }

  return file
}



@Composable
fun PrimaryButton(
  text: String, 
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  Button(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    enabled = enabled
  ) {
    Text(text)
  }
} 