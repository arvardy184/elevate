package com.application.elevate.ui.navigation

import com.application.elevate.R
import com.application.elevate.model.NavbarItem

val navbar = listOf(
    NavbarItem("home", R.drawable.ic_home, R.string.nav_home),
    NavbarItem("my_course", R.drawable.ic_course, R.string.nav_my_course),
    NavbarItem("filter", R.drawable.ic_filter_center, R.string.nav_filter),
    NavbarItem("message", R.drawable.ic_message, R.string.nav_message),
    NavbarItem("profile", R.drawable.ic_profile, R.string.nav_profile)
)