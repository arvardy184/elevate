package com.application.elevate.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.elevate.model.QuizItem
import com.application.elevate.model.VideoItem

@Composable
fun VideoItemView(item: VideoItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(3.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.thumbnail),
            contentDescription = item.title,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = item.title, fontWeight = FontWeight.SemiBold)
            Text(text = item.duration, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun QuizItemView(item: QuizItem) {
    // sama persis dengan VideoItemView
    VideoItemView(
        item = VideoItem(item.title, item.duration, item.thumbnail)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewVideoItemView() {
    // Dummy data untuk preview
    val dummyVideoItem = VideoItem(
        title = "Introduction to Kotlin",
        duration = "5 min",
        thumbnail = android.R.drawable.ic_media_play // Thumbnail dummy bawaan Android
    )

    VideoItemView(item = dummyVideoItem)
}
