package com.application.elevate.util

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {
    private var snackbarHostState: SnackbarHostState? = null
    private var coroutineScope: CoroutineScope? = null
    
    fun setSnackbarHost(snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
        this.snackbarHostState = snackbarHostState
        this.coroutineScope = scope
    }
    
    fun showQuizSyncSuccess(message: String) {
        // Show snackbar if available
        snackbarHostState?.let { snackbar ->
            coroutineScope?.launch {
                snackbar.showSnackbar(
                    message = "✅ $message",
                    withDismissAction = true
                )
            }
        }
        
        // Fallback to toast
        Toast.makeText(context, "✅ $message", Toast.LENGTH_LONG).show()
    }
    
    fun showSyncError(message: String) {
        // Show snackbar if available
        snackbarHostState?.let { snackbar ->
            coroutineScope?.launch {
                snackbar.showSnackbar(
                    message = "❌ $message",
                    withDismissAction = true
                )
            }
        }
        
        // Fallback to toast
        Toast.makeText(context, "❌ $message", Toast.LENGTH_LONG).show()
    }
}

// Composition local for accessing notification manager
val LocalNotificationManager = staticCompositionLocalOf<NotificationManager?> { null } 