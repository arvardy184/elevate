package com.application.elevate.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.application.elevate.R
import com.application.elevate.model.User
import coil.compose.AsyncImage


@Composable
fun HeaderCard(
    user: User,
    onNotificationClick: () -> Unit,
    onSearchClick: () -> Unit, // ✅ Tambahan ini
    onSearchBarPositioned: (Offset, Size) -> Unit,  // << Tambahan
    modifier: Modifier = Modifier

) {

    var searchBarPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var searchBarSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

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
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        if (user.getFullProfilePictureUrl()?.isNotEmpty() == true) {
                            AsyncImage(
                                model = user.getFullProfilePictureUrl(),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "Profile Picture Placeholder",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick() } // Pindah ke luar
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = {Text(
                        "Search here...",
                        color = Color.Gray // Custom color for placeholder text
                    )},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray // Custom color for icon
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            val position = coordinates.positionInRoot()
                            val size = coordinates.size.toSize()
                            onSearchBarPositioned(position, size) // ⬅️ KIRIM BALIK KE HOMESCREEN
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.95f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                        disabledContainerColor = Color.White.copy(alpha = 0.85f) // Keep same color when disabled
                    )
                )
            }
        }
    }
}