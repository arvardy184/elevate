package com.application.elevate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.model.JobMatchingHistoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PendingHistoryCard(
    historyItem: JobMatchingHistoryItem,
    isPending: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPending) {
                Color(0xFFFFF3E0) // Light Orange for pending
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = historyItem.dreamJob,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (isPending) {
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFF9800)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Pending",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Text(
                                    text = "PENDING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                
                Text(
                    text = formatDate(historyItem.createdAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${historyItem.matches.size} matches ditemukan",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (isPending) {
                    Text(
                        text = "Akan disinkronkan saat online",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (isPending) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Data ini disimpan saat offline dan akan disinkronkan ketika koneksi internet tersedia.",
                    fontSize = 12.sp,
                    color = Color(0xFFBF6000),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
} 