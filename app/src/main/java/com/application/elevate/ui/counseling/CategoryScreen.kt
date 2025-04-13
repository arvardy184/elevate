package com.application.elevate.ui.counseling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.elevate.component.CategoryItem
import com.application.elevate.ui.theme.ReplyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    fun CategoryScreen(onSelect: (String) -> Unit) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            TopAppBar(title = { Text("Counseling") }, )
//                colors = TopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            LazyRow(modifier = Modifier.padding(16.dp)) {
                items(listOf("Design","Development","Finance","Programming")) { cat ->
                    CategoryItem(
                        label = cat.toString(),
                        modifier = Modifier.padding(end = 8.dp)
                    ) { onSelect(cat.toString()) }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewCategoryScreen() {
        ReplyTheme { CategoryScreen(onSelect = {}) }
    }