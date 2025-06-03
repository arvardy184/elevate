package com.application.elevate.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.model.CategoryCourse

@Composable
fun CategoryCourseItem(
    course: CategoryCourse,
    onCourseClick: (CategoryCourse) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCourseClick(course) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            // Gambar kursus
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {
                Image(
                    painter = painterResource(id = course.imageRes),
                    contentDescription = course.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Informasi kursus
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Judul kursus
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Durasi dan jumlah pelajaran
                Text(
                    text = "${course.duration} • ${course.lessons} lesson",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress dan status kursus
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (course.isCompleted) {
                        Text(
                            text = "100%\nCompleted",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    } else {
                        // Tempat kosong jika tidak ada progress
                        Spacer(modifier = Modifier.width(60.dp))
                    }
                    
                    Button(
                        onClick = { onCourseClick(course) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(40.dp)
                    ) {
                        Text(
                            text = if (course.isCompleted) "Open Course" else "Buy Course",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
} 