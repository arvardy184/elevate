package com.application.elevate.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.R
import com.application.elevate.model.User
import com.application.elevate.ui.theme.Purple5
import com.application.elevate.ui.theme.Purple6
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun ProfileHeader(
    user: User,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Purple6, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                if (user.photoUrl.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(500.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = user.fullName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
fun ProfileHeaderPreview() {
    ReplyTheme {
        ProfileHeader(
            user = User(
                firstName = "Keisya Marshanda",
                lastName = "Setiandini"
            ),
            onProfileClick = {}
        )
    }
}