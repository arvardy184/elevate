package com.application.elevate.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.elevate.R
import com.application.elevate.model.User


@Composable
fun HeaderCard(
    user: User,
    onNotificationClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            )
    ) {
        // 🎨 Background dari drawable
        Image(
            painter = painterResource(id = R.drawable.header_bg), // ganti sesuai nama drawable kamu
            contentDescription = "Header Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
        )

        // 🧱 Konten di atas background
        Column(modifier = Modifier.padding(top = 42.dp, start = 16.dp, end = 16.dp, bottom = 42.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {


                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Welcome,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            user.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )
                    }

                }

                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search here...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Filter",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.95f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
                )
            )
        }
    }
}