// package com.application.elevate.ui.cvreview

// import android.app.DownloadManager
// import android.content.Context
// import android.net.Uri
// import android.os.Environment
// import android.widget.Toast
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.remember
// import androidx.compose.ui.platform.LocalContext

// class FileDownloader(private val context: Context) {
  
//   fun downloadFile(url: String, fileName: String) {
//     try {
//       val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
      
//       val request = DownloadManager.Request(Uri.parse(url)).apply {
//         setTitle("Downloading $fileName")
//         setDescription("CV sedang didownload...")
//         setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//         setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
//         setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//       }
      
//       downloadManager.enqueue(request)
//       Toast.makeText(context, "Download dimulai...", Toast.LENGTH_SHORT).show()
      
//     } catch (e: Exception) {
//       Toast.makeText(context, "Download gagal: ${e.message}", Toast.LENGTH_LONG).show()
//     }
//   }
// }

