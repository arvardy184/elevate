package com.application.elevate.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.android.identity.documenttype.Icon;
import com.application.elevate.R
import com.application.elevate.model.HelpCenterItem
import com.application.elevate.ui.theme.Neutral5
import com.application.elevate.ui.theme.Neutral7
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun ExpandableHelpItem(
        item: HelpCenterItem,
        onToggleExpand: () -> Unit
) {
val rotationState by animateFloatAsState(
        targetValue = if (item.isExpanded) 180f else 0f,
label = "rotation"
        )

Column(
        modifier = Modifier
        .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
Row(
        modifier = Modifier
        .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(vertical = 8.dp),
verticalAlignment = Alignment.CenterVertically
        ) {
Text(
        text = item.question,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.weight(1f)
            )

Icon(
        imageVector = Icons.Filled.KeyboardArrowDown,
contentDescription = if (item.isExpanded) "Collapse" else "Expand",
tint = MaterialTheme.colorScheme.onBackground,
modifier = Modifier
        .size(24.dp)
                    .rotate(rotationState)
            )
                    }

AnimatedVisibility(visible = item.isExpanded) {
    Text(
            text = item.answer,
            fontSize = 14.sp,
            color = Neutral7,
            modifier = Modifier.padding(bottom = 8.dp)
    )
}

Divider(
        color = Neutral5,
        thickness = 1.dp
)
    }
            }

@Preview(showBackground = true)
@Composable
fun ExpandableHelpItemPreview() {
    ReplyTheme {
        ExpandableHelpItem(
                item = HelpCenterItem(
                        id = "1",
                        question = "What is elevate?",
                        answer = "Elevate is a learning platform that helps you improve your skills and knowledge.",
                        isExpanded = true
                ),
                onToggleExpand = {}
        )
    }
}