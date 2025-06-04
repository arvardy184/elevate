package com.application.elevate.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.elevate.R
import com.application.elevate.model.User
import coil.compose.AsyncImage


@Composable
fun RoadmapHeader(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            ) {
                if (user.photoUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "User Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.profile_placeholder),
                        placeholder = painterResource(id = R.drawable.profile_placeholder)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "User Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = "Hi, ${user.firstName}!", style = MaterialTheme.typography.titleMedium)
                Text(text = "Designer", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "10/50", // asumsi crown point dummy
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .background(Color.Yellow, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Image(
                painter = painterResource(id = R.drawable.ic_gift),
                contentDescription = "Gift",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
