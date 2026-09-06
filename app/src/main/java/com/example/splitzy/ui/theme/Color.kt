package com.example.splitzy.ui.theme

import androidx.compose.ui.graphics.Color

// Matches the reference design: warm peach-cream background, a deep forest
// green brand color (not a bright mint), and group types as solid, fully
// saturated card fills rather than subtle accents.

val ForestPrimaryLight = Color(0xFF1F5D40)
val OnForestPrimaryLight = Color(0xFFFFFFFF)
val ForestPrimaryContainerLight = Color(0xFFCFE9DA)
val OnForestPrimaryContainerLight = Color(0xFF00210F)

val OrangeSecondaryLight = Color(0xFFF2994A)
val OnOrangeSecondaryLight = Color(0xFFFFFFFF)
val OrangeSecondaryContainerLight = Color(0xFFFFE3C4)
val OnOrangeSecondaryContainerLight = Color(0xFF4A2800)

val BackgroundLight = Color(0xFFFBF1E6)
val SurfaceLight = Color(0xFFFFFCF7)
val SurfaceVariantLight = Color(0xFFF0E6D6)
val OnSurfaceLight = Color(0xFF241F17)
val OnSurfaceVariantLight = Color(0xFF5A5343)
val OutlineLight = Color(0xFF8C8271)
val OutlineVariantLight = Color(0xFFDFD3BE)

val ForestPrimaryDark = Color(0xFF7ED0A8)
val OnForestPrimaryDark = Color(0xFF00391F)
val ForestPrimaryContainerDark = Color(0xFF0B4A2E)
val OnForestPrimaryContainerDark = Color(0xFFCFE9DA)

val OrangeSecondaryDark = Color(0xFFFFB877)
val OnOrangeSecondaryDark = Color(0xFF4A2800)
val OrangeSecondaryContainerDark = Color(0xFF6B3E0F)
val OnOrangeSecondaryContainerDark = Color(0xFFFFE3C4)

// Warm charcoal instead of pure black — reads as dim room lighting, not "OLED off".
val BackgroundDark = Color(0xFF1E1B14)
val SurfaceDark = Color(0xFF25211A)
val SurfaceVariantDark = Color(0xFF4C4636)
val OnSurfaceDark = Color(0xFFEDE6D8)
val OnSurfaceVariantDark = Color(0xFFD1C8B5)
val OutlineDark = Color(0xFF999080)
val OutlineVariantDark = Color(0xFF4C4636)

// Settlement colors: independent of primary/secondary so "money in / money out"
// reads consistently regardless of theme.
val MoneyIn = Color(0xFF3F8455)
val MoneyInContainer = Color(0xFFDCEBDC)
val MoneyOut = Color(0xFFB5473A)
val MoneyOutContainer = Color(0xFFF6DFD9)

// Group-type card fills — solid, fully saturated, white content on top of
// them (not theme-adaptive: these are brand accents, same in light and dark).
val HomeAccent = Color(0xFF1F5D40)
val TripAccent = Color(0xFFF2994A)
val EventAccent = Color(0xFFE85D9C)
