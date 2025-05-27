package com.application.elevate.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.elevate.component.CategoryGridItem
import com.application.elevate.component.Navbar
import com.application.elevate.model.Category
import com.application.elevate.ui.category.CategoryViewModel
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header dengan gradient background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF7E5F),
                                Color(0xFF845EC2)
                            )
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp) // Atur tinggi sesuai kebutuhan
                    ){
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Judul Categories
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                        )

                    }

                    // Tombol Back

                }

                Spacer(modifier = Modifier.height(25.dp))

                
                // Subtitle
                Text(
                    text = "Choose your subject!",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                )
            }
            
            // Grid kategori
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(y = (-50).dp)
                    .clip(RoundedCornerShape(topStart = 55.dp, topEnd = 55.dp))
                    .background(Color.White)
                    .padding(top = 20.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.categories) { category ->
                        CategoryGridItem(
                            category = category,
                            onClick = { 
                                // Navigate to CategoryCoursesScreen with the category ID
                                navController.navigate("category_courses/${category.id}")
                            }
                        )
                    }
                }
            }
        }
        
        // Bottom navbar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 0.dp)
        ) {
            Navbar(
                navController = navController,
                onItemClick = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    ReplyTheme {
        CategoryScreen(
            navController = rememberNavController()
        )
    }
} 