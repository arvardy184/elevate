package com.application.elevate.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.elevate.R
import com.application.elevate.model.CounselingCategory
import com.application.elevate.ui.theme.Purple4

@Composable
fun CategoryCounselingItem(category: CounselingCategory, modifier: Modifier = Modifier,    isSelected: Boolean = false,
                           onClick: () -> Unit // Tambahkan ini
) {

    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else Purple4

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() } // Trigger aksi saat diklik

    ) {
        Image(
            painter = painterResource(id = category.iconResId),
            contentDescription = category.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFC081A9), Color(0xFFFFBE9D)), // Ungu ke biru
                        start = Offset(0f, 0f),           // Titik mulai (atas)
                        end = Offset(0f, 1000f)   // arah horizontal
                    )
                )                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemConselingPreview() {
    CategoryCounselingItem(
        category = CounselingCategory(
            id = "1",
            name = "Design",
            iconResId = R.drawable.ic_category_conseling_design
        ), onClick = {}
    )
}
