package com.application.elevate.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.application.elevate.model.NotificationItem
import com.application.elevate.R
import com.application.elevate.ui.theme.Purple2
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun NotificationItemCard(item: NotificationItem) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(colors.surface, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.avatarUrl ?: R.drawable.home_nav,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Purple2)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface
            )
            Text(
                text = item.subtitle,
                style = typography.bodyMedium,
                color = colors.onSurface.copy(alpha = 0.7f)
            )
        }

        Text(
            text = item.time,
            style = typography.bodySmall,
            color = colors.onSurface.copy(alpha = 0.6f)
        )
    }
}


// Pratinjau untuk NotificationScreen
@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    ReplyTheme {
        NotificationScreen()
    }
}