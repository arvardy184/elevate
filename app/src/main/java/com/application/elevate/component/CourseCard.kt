package com.application.elevate.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.data.dummyCourses
import com.application.elevate.model.Course
import com.application.elevate.ui.login.poppinsFontFamily


@Composable
fun CourseCard(course: Course) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(240.dp)
            .wrapContentHeight(),
//        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background).shadow(2.dp, RoundedCornerShape(20.dp))
        ) {

            // 📷 Course Image
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = course.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
            )

            // 📋 Detail
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${course.duration} • ${course.lessons} lesson",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${course.progressPercent}% \nCompleted",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (course.progressPercent == 100) Color(0xFF4CAF50) else Color.Gray
                    )


                    AssistChip(
                        onClick = { /* TODO */ },
                        label = {
                            Text(
                                text = "Open Course",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            labelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseCardPreview() {
    CourseCard(course = dummyCourses[0])
}