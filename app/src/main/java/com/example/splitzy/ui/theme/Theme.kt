package com.example.splitzy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestPrimaryLight,
    onPrimary = OnForestPrimaryLight,
    primaryContainer = ForestPrimaryContainerLight,
    onPrimaryContainer = OnForestPrimaryContainerLight,
    secondary = OrangeSecondaryLight,
    onSecondary = OnOrangeSecondaryLight,
    secondaryContainer = OrangeSecondaryContainerLight,
    onSecondaryContainer = OnOrangeSecondaryContainerLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = ForestPrimaryDark,
    onPrimary = OnForestPrimaryDark,
    primaryContainer = ForestPrimaryContainerDark,
    onPrimaryContainer = OnForestPrimaryContainerDark,
    secondary = OrangeSecondaryDark,
    onSecondary = OnOrangeSecondaryDark,
    secondaryContainer = OrangeSecondaryContainerDark,
    onSecondaryContainer = OnOrangeSecondaryContainerDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

// Dynamic color is deliberately not offered: it would replace this palette
// with whatever's in the user's wallpaper, which undoes the point of picking one.
@Composable
fun SplitzyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
