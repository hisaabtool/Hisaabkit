package com.hisaabkit.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = HisaabKitPrimary,
    onPrimary = HisaabKitOnPrimary,

    primaryContainer = HisaabKitPrimaryContainer,
    onPrimaryContainer = HisaabKitOnPrimaryContainer,

    secondary = HisaabKitPurple,
    tertiary = HisaabKitOrange,

    background = HisaabKitBackground,
    surface = HisaabKitSurface,
    surfaceContainer = HisaabKitCard
)

private val DarkColors = darkColorScheme(
    primary = HisaabKitPrimaryDark,
    onPrimary = HisaabKitOnPrimaryDark,

    primaryContainer = HisaabKitPrimaryContainerDark,
    onPrimaryContainer = HisaabKitOnPrimaryContainerDark,

    secondary = HisaabKitPurple,
    tertiary = HisaabKitOrange,

    background = HisaabKitBackgroundDark,
    surface = HisaabKitSurfaceDark
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
