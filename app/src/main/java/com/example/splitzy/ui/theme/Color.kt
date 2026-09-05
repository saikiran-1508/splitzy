package com.example.splitzy.ui.theme

import androidx.compose.ui.graphics.Color

// A brighter, more colorful pass on the earlier muted sage/terracotta palette —
// same warm-cream base (not gray, not stark white), but the brand colors and
// the three group-type accents are more saturated so the app reads as lively
// rather than muted, without tipping into neon.

val EmeraldPrimaryLight = Color(0xFF1E9E6B)
val OnEmeraldPrimaryLight = Color(0xFFFFFFFF)
val EmeraldPrimaryContainerLight = Color(0xFFC8F2DD)
val OnEmeraldPrimaryContainerLight = Color(0xFF003821)

val CoralSecondaryLight = Color(0xFFF4743B)
val OnCoralSecondaryLight = Color(0xFFFFFFFF)
val CoralSecondaryContainerLight = Color(0xFFFFE0CC)
val OnCoralSecondaryContainerLight = Color(0xFF4A1D00)

val BackgroundLight = Color(0xFFFAF8F2)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFEFEAD8)
val OnSurfaceLight = Color(0xFF23241D)
val OnSurfaceVariantLight = Color(0xFF58564A)
val OutlineLight = Color(0xFF83806E)
val OutlineVariantLight = Color(0xFFD9D3C0)

val EmeraldPrimaryDark = Color(0xFF6EE6B0)
val OnEmeraldPrimaryDark = Color(0xFF00391F)
val EmeraldPrimaryContainerDark = Color(0xFF00543A)
val OnEmeraldPrimaryContainerDark = Color(0xFFC8F2DD)

val CoralSecondaryDark = Color(0xFFFFB08A)
val OnCoralSecondaryDark = Color(0xFF4A1D00)
val CoralSecondaryContainerDark = Color(0xFF6B3210)
val OnCoralSecondaryContainerDark = Color(0xFFFFE0CC)

// Warm charcoal instead of pure black — reads as dim room lighting, not "OLED off".
val BackgroundDark = Color(0xFF1C1B15)
val SurfaceDark = Color(0xFF232019)
val SurfaceVariantDark = Color(0xFF4A4436)
val OnSurfaceDark = Color(0xFFECE7D8)
val OnSurfaceVariantDark = Color(0xFFCFC9B6)
val OutlineDark = Color(0xFF948F7C)
val OutlineVariantDark = Color(0xFF4A4436)

// Settlement colors: independent of primary/secondary so "money in / money out"
// reads consistently regardless of theme.
val MoneyIn = Color(0xFF3F8455)
val MoneyInContainer = Color(0xFFDCEBDC)
val MoneyOut = Color(0xFFB5473A)
val MoneyOutContainer = Color(0xFFF6DFD9)

// One distinct, vivid color per group type — this is where "colorful" mostly
// shows up, since it's the one place three hues legitimately coexist on screen.
val HomeAccent = Color(0xFF22B37D)
val TripAccent = Color(0xFFF4743B)
val EventAccent = Color(0xFFE0559C)
