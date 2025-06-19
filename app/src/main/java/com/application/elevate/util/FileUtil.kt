package com.application.elevate.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtil {
    
    /**
     * Create a File from URI for upload purposes
     */
    fun createFileFromUri(context: Context, uri: Uri, fileName: String): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            
            if (inputStream == null) {
                Log.e("FileUtil", "Failed to open input stream from URI")
                return null
            }
            
            // Create temporary file in cache directory
            val tempFile = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(tempFile)
            
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            
            Log.d("FileUtil", "File created successfully: ${tempFile.absolutePath}")
            tempFile
        } catch (e: IOException) {
            Log.e("FileUtil", "Error creating file from URI", e)
            null
        }
    }
    
    /**
     * Get file name from URI
     */
    fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val contentResolver = context.contentResolver
        
        try {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    if (displayNameIndex >= 0) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FileUtil", "Error getting file name", e)
        }
        
        return fileName ?: "cv.pdf"
    }
    
    /**
     * Get file size from URI
     */
    fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(MediaStore.MediaColumns.SIZE)
                    if (sizeIndex >= 0) {
                        return it.getLong(sizeIndex)
                    }
                }
            }
            0L
        } catch (e: Exception) {
            Log.e("FileUtil", "Error getting file size", e)
            0L
        }
    }
    
    /**
     * Check if file is PDF
     */
    fun isPdfFile(context: Context, uri: Uri): Boolean {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
        return mimeType == "application/pdf"
    }
    
    /**
     * Format file size for display
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> "%.2f MB".format(bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> "%.2f KB".format(bytes / 1024.0)
            else -> "$bytes B"
        }
    }
} 