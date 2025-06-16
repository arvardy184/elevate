package com.application.elevate.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerFieldDropdown(
  selectedCareerField: String,
  onCareerFieldSelected: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  val careerFields = listOf(
    "Software Engineer",
    "Data Scientist", 
    "Product Manager",
    "UI/UX Designer",
    "Marketing",
    "Finance",
    "Other"
  )

  Column {
    Text(
      text = "Target Career Field",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    
    ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = { expanded = !expanded }
    ) {
      OutlinedTextField(
        value = selectedCareerField,
        onValueChange = { },
        readOnly = true,
        placeholder = { Text("Select career field") },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        modifier = Modifier
          .menuAnchor()
          .fillMaxWidth()
      )
      
      ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
      ) {
        careerFields.forEach { field ->
          DropdownMenuItem(
            text = { Text(field) },
            onClick = {
              onCareerFieldSelected(field)
              expanded = false
            }
          )
        }
      }
    }
  }
} 