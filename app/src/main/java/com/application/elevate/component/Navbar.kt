package com.application.elevate.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.application.elevate.R
import com.application.elevate.model.NavItem
import com.application.elevate.ui.theme.ReplyTheme


@Composable
fun Navbar(
    selectedRoute: String,
    onItemClick: (String) -> Unit
) {
    val navItems = listOf(
        NavItem("Home", R.drawable.home_nav, "home"),
        NavItem("Course", R.drawable.course_nav, "course"),
        NavItem("Center", R.drawable.roadmap_nav, "center"),
        NavItem("Message", R.drawable.message_nav, "message"),
        NavItem("Profile", R.drawable.profile_nav, "profile"),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Color.Transparent),


    ) {
        Image(
            painter = painterResource(id = R.drawable.navbar_background), // ubah ke nama file kamu
            contentDescription = "Navbar Background",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .matchParentSize()
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            // atau .SpaceBetween, .Center, dll
            verticalAlignment = Alignment.Bottom
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = item.route == selectedRoute
                val iconColor = if (isSelected) Color.Black else Color.Gray

                if (index == 2) {
                    // ⬆️ Item tengah beda desain
                    Box(
                        modifier = Modifier
                            .offset(y = (-15).dp)
                            .size(56.dp)
                            .border(4.dp, Color.Black, RoundedCornerShape(50))
                            .padding(3.dp)
                            .clip(CircleShape)
                            .clickable { onItemClick(item.route) },
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.main_button), // drawable bg kamu
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                        )

                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.title,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onItemClick(item.route) },
                        modifier = Modifier.width(10.dp).height(73.dp).padding(top = 30.dp),
                        icon = {
                            androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    tint = iconColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = item.title,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Start,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = iconColor
                                )
                            }
                        },
                        alwaysShowLabel = false // tetap false karena kita custom sendiri label-nya
                    )
                }
            }
        }
    }



}

@Preview(showBackground = true)
@Composable
fun HomeScreenPrevie() {
    ReplyTheme { // Pastikan ini adalah theme kamu
        Navbar(
            selectedRoute = "",
            onItemClick = { route ->
                // TODO: navigate ke halaman lain
                Log.d("BottomNavBar", "Navigasi ke: $route")
            }
        )
    }
}