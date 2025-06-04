package com.application.elevate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple5,
    onPrimary = Neutral1,
    onSecondaryContainer = Purple2,
    secondary = Purple2,
    onSecondary = Neutral1,
    background = Neutral1,
    onBackground = Neutral10,
    surface = Neutral3,
    onSurface = Neutral10,
    error = Red6,
    onError = Neutral1
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple2,
    onPrimary = Neutral13,
    secondary = Orange2,
    onSecondary = Neutral13,
    onSecondaryContainer = Purple2,
    background = Neutral13,
    onBackground = Neutral1,
    surface = Neutral11,
    onSurface = Neutral1,
    error = Red3,
    onError = Neutral13
)

@Composable
fun ReplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (!darkTheme) {
            LightColorScheme
        } else {
            DarkColorScheme
        }
    
    // Ini akan menjamin semua Text dalam scope ini menggunakan font Poppins
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = PoppinsFontFamily)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}


