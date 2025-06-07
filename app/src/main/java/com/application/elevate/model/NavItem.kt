package com.application.elevate.model


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class NavItem(
    val title: String,
    val iconRes: Int,
    val route: String
)
