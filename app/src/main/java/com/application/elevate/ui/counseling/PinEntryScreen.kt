package com.application.elevate.ui.counseling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.application.elevate.ui.theme.ReplyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinEntryScreen(onPinComplete: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Enter Your Pin") },
            navigationIcon = {
                IconButton(onClick = { /* TODO: navBack */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(Modifier.height(32.dp))
        Text("Enter your 6 digit pin", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(6) { index ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pin.getOrNull(index)?.toString() ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        // invisible field to capture numeric input
        BasicTextField(
            value = pin,
            onValueChange = {
                if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                    pin = it
                    if (it.length == 6) onPinComplete(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(0.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPinEntryScreen() {
    ReplyTheme {
        PinEntryScreen(onPinComplete = {})
    }
}
