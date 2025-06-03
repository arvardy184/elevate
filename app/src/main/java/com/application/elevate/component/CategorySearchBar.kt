package com.application.elevate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun CategorySearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search Here...",
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF3F2F7))
    ) {
        // Search icon
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )
        
        // Text field
        BasicTextField(
            value = searchText,
            onValueChange = { 
                searchText = it
                onSearch(it)
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 56.dp, end = 16.dp)
                .align(Alignment.Center)
        )
        
        // Hint text
        if (searchText.isEmpty()) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 16.dp)
                    .align(Alignment.Center)
            )
        }
    }
} 