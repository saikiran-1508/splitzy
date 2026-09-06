package com.example.splitzy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldLight,
    onPrimary = OnEmeraldLight,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = OnEmeraldContainerLight,
    secondary = CoralLight,
    onSecondary = OnCoralLight,
    secondaryContainer = CoralContainerLight,
    onSecondaryContainer = OnCoralContainerLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldDark,
    onPrimary = OnEmeraldDark,
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = OnEmeraldContainerDark,
    secondary = CoralDark,
    onSecondary = OnCoralDark,
    secondaryContainer = CoralContainerDark,
    onSecondaryContainer = OnCoralContainerDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

// Group-type and money colours don't map onto Material's slots, so they ride
// alongside the scheme instead of being read off isSystemInDarkTheme() at each
// call site — that way an explicitly-themed subtree still gets the right ones.
@Immutable
data class SplitzyAccents(
    val homeAccent: Color,
    val homeContainer: Color,
    val tripAccent: Color,
    val tripContainer: Color,
    val eventAccent: Color,
    val eventContainer: Color,
    val moneyIn: Color,
    val moneyOut: Color
)

private val LightAccents = SplitzyAccents(
    homeAccent = HomeAccentLight,
    homeContainer = HomeContainerLight,
    tripAccent = TripAccentLight,
    tripContainer = TripContainerLight,
    eventAccent = EventAccentLight,
    eventContainer = EventContainerLight,
    moneyIn = MoneyInLight,
    moneyOut = MoneyOutLight
)

private val DarkAccents = SplitzyAccents(
    homeAccent = HomeAccentDark,
    homeContainer = HomeContainerDark,
    tripAccent = TripAccentDark,
    tripContainer = TripContainerDark,
    eventAccent = EventAccentDark,
    eventContainer = EventContainerDark,
    moneyIn = MoneyInDark,
    moneyOut = MoneyOutDark
)

val LocalSplitzyAccents = staticCompositionLocalOf { LightAccents }

object SplitzyTheme {
    val accents: SplitzyAccents
        @Composable get() = LocalSplitzyAccents.current
}

// Dynamic colour is deliberately not offered: it would replace this palette
// with whatever's in the user's wallpaper, which undoes the point of picking one.
@Composable
fun SplitzyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSplitzyAccents provides if (darkTheme) DarkAccents else LightAccents
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
