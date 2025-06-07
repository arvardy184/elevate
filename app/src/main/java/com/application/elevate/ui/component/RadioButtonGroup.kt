package com.application.elevate.ui.component
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun RadioButtonGroup(
    title: String,
    subTitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(subTitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))


        options.forEach { option ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(vertical = 4.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .clickable { onOptionSelected(option) }, // ✅ Trigger change
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = null // ✅ Prevent overriding parent's clickable
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(option)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRadioButtonGroup() {
    var selectedOption by remember { mutableStateOf("Option 1") }

    RadioButtonGroup(
        title = "Select an option",
        subTitle = "testsdas dsadjkahduiwad",
        options = listOf("Option 1", "Option 2", "Option 3"),
        selectedOption = selectedOption,
        onOptionSelected = { selectedOption = it }
    )
}