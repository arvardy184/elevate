package com.application.elevate.ui.cvreview


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CVFormatSelector(selectedFormat: String, onFormatSelected: (String) -> Unit) {
    Row(modifier = Modifier.padding(8.dp)) {
        listOf("ATS", "Personal Design").forEach { format ->
            Row(modifier = Modifier.padding(end = 16.dp)) {
                Icon(
                    imageVector = Icons.Filled.Description, // Gunakan icon dokumen untuk format
                    contentDescription = "Document Icon",
                    modifier = Modifier.size(24.dp)
                )
                RadioButton(
                    selected = selectedFormat == format,
                    onClick = { onFormatSelected(format) },
                    colors = RadioButtonDefaults.colors()
                )
                Text(text = format, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
