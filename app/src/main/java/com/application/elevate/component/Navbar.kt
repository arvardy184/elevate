package com.application.elevate.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R
import com.application.elevate.model.NavItem
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun Navbar(
    navController: NavController
) {
    val navItems = listOf(
        NavItem("Home", R.drawable.home_nav, "home"),
        NavItem("Course", R.drawable.course_nav, "course"),
        NavItem("Center", R.drawable.roadmap_nav, "center"),
        NavItem("Message", R.drawable.message_nav, "message"),
        NavItem("Profile", R.drawable.profile_nav, "profile"),
    )

    // Ambil current route dari NavController
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Transparent)
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
                .height(76.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = item.route == currentRoute
                val iconColor = if (isSelected) Color.Black else Color.Gray

                if (index == 2) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-15).dp)
                            .size(60.dp)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigate(item.route) },
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
        Navbar(navController = navController)
    }
}