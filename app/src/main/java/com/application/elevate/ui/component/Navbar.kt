package com.application.elevate.ui.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R
import com.application.elevate.model.NavItem
import com.application.elevate.model.matchesRoute
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun Navbar(
    navController: NavController,
    onItemClick: (String) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Log.d("Navbar", "Current route: $currentRoute")


    val navItems = listOf(
        NavItem("Home", R.drawable.home_nav, "home"),
        NavItem("Course", R.drawable.course_nav, "course"),
        NavItem("Center", R.drawable.roadmap_nav, "roadmap"),
        NavItem("Message", R.drawable.message_nav, "message"),
        NavItem("Profile", R.drawable.profile_nav, "profile"),
    )

    // Ambil current route dari NavController

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Transparent),
    ) {
        Image(
            painter = painterResource(id = R.drawable.navbar_background),
            contentDescription = "Navbar Background",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.matchParentSize()
        )



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(76.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = currentRoute?.matchesRoute(item.route) == true
                val iconColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                if (index == 2) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-15).dp)
                            .size(60.dp)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.main_button),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.title,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(item.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.title,
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = iconColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavbarPreview() {
    ReplyTheme {
        val navController = rememberNavController()
        Navbar(navController = navController, onItemClick = {route -> navController.navigate(route)})
    }
}