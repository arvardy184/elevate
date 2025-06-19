package com.application.elevate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SyncStatusCard(
    isOffline: Boolean,
    unsyncedCount: Int,
    isSyncing: Boolean,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (unsyncedCount > 0 || isOffline) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOffline) {
                    Color(0xFFFFECB3) // Light Orange
                } else {
                    Color(0xFFE3F2FD) // Light Blue
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.CloudSync,
                    contentDescription = if (isOffline) "Offline" else "Sync",
                    tint = if (isOffline) Color(0xFFFF9800) else Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isOffline) {
                            "Kamu sedang offline"
                        } else {
                            "$unsyncedCount data belum tersinkronisasi"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isOffline) Color(0xFFE65100) else Color(0xFF1976D2)
                    )
                    
                    if (!isOffline && unsyncedCount > 0) {
                        Text(
                            text = "Tekan tombol sync untuk menyinkronkan data",
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2).copy(alpha = 0.7f)
                        )
                    }
                }
                
                if (!isOffline && unsyncedCount > 0) {
                    Button(
                        onClick = onSyncClick,
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sync",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
} 