package com.application.elevate.model


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class NavbarItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
)
