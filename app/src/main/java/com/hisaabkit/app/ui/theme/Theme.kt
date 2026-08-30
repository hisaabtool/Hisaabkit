package com.hisaabkit.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = HisaabKitPrimary,
    onPrimary = HisaabKitOnPrimary,

    primaryContainer = HisaabKitPrimaryContainer,
    onPrimaryContainer = HisaabKitOnPrimaryContainer,

    secondary = HisaabKitPurple,
    onSecondary = Color.White,

    tertiary = HisaabKitOrange,
    onTertiary = Color.White,

    background = HisaabKitBackground,
    onBackground = HisaabKitOnPrimaryContainer,

    surface = HisaabKitSurface,
    onSurface = HisaabKitOnPrimaryContainer,

    surfaceVariant = HisaabKitCard,
    onSurfaceVariant = HisaabKitOnPrimaryContainer
)

private val DarkColors = darkColorScheme(
    primary = HisaabKitPrimaryDark,
    onPrimary = HisaabKitOnPrimaryDark,

    primaryContainer = HisaabKitPrimaryContainerDark,
    onPrimaryContainer = HisaabKitOnPrimaryContainerDark,

    secondary = HisaabKitPurple,
    onSecondary = Color.White,

    tertiary = HisaabKitOrange,
    onTertiary = Color.White,

    background = HisaabKitBackgroundDark,
    onBackground = HisaabKitOnPrimaryDark,

    surface = HisaabKitSurfaceDark,
    onSurface = HisaabKitOnPrimaryDark,

    surfaceVariant = Color(0xFF2A2730),
    onSurfaceVariant = HisaabKitOnPrimaryDark
)

@Composable
fun HisaabKitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = HisaabKitTypography,
        content = content
    )
}
