package com.application.elevate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.R
import com.application.elevate.ui.theme.*

@Composable
fun ProfileMenuItem(
    icon: Any, // Bisa ImageVector atau Int (resource ID)
    title: String,
    subtitle: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconBackground: Color = Purple1,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            when (icon) {
                is ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                is Int -> {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                else -> {
                    // Fallback jika tipe tidak sesuai
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Neutral7
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowRight,
            contentDescription = "Navigate",
            tint = Neutral7,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Overload untuk ImageVector
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconBackground: Color = Purple1,
    onClick: () -> Unit
) {
    ProfileMenuItem(
        icon = icon as Any,
        title = title,
        subtitle = subtitle,
        iconTint = iconTint,
        iconBackground = iconBackground,
        onClick = onClick
    )
}

// Overload untuk resource ID
@Composable
fun ProfileMenuItem(
    iconResId: Int,
    title: String,
    subtitle: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconBackground: Color = Purple1,
    onClick: () -> Unit
) {
    ProfileMenuItem(
        icon = iconResId as Any,
        title = title,
        subtitle = subtitle,
        iconTint = iconTint,
        iconBackground = iconBackground,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileMenuItemPreview() {
    ReplyTheme {
        ProfileMenuItem(
            icon = Icons.Default.ArrowForward,
            title = "Profile Settings",
            subtitle = "Customize your personal information",
            onClick = {}
        )
    }
}