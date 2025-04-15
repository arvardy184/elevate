package com.application.elevate.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.elevate.ui.notification.NotificationViewModel
import com.application.elevate.component.TopBarTitle
import com.application.elevate.component.SectionHeader
import com.application.elevate.model.NotificationItem
import com.application.elevate.ui.notification.NotificationItemCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        TopBarTitle(title = "Notification")

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            val grouped = notifications.groupBy { it.dateLabel }

            grouped.forEach { (label, items) ->
                item {
                    SectionHeader(title = label ?: "Unknown Date", onViewAllClick = {})
                }
                items(items) { notif ->
                    NotificationItemCard(notif)
                }
            }
        }
    }
}

@Preview
@Composable
fun NotificationScreenPreview() {
    ReplyTheme {
        NotificationScreen()
    }
}
