package com.example.splitzy.ui.theme

import androidx.compose.ui.graphics.Color

// Deep teal is the brand color — trustworthy, money-adjacent without being
// the generic green every fintech app defaults to. Amber is the one accent,
// used sparingly (FABs, the "you get paid" pill) so it stays an accent.

val TealPrimaryLight = Color(0xFF146356)
val OnTealPrimaryLight = Color(0xFFFFFFFF)
val TealPrimaryContainerLight = Color(0xFFCDEEE4)
val OnTealPrimaryContainerLight = Color(0xFF00201A)

val AmberSecondaryLight = Color(0xFFB4590A)
val OnAmberSecondaryLight = Color(0xFFFFFFFF)
val AmberSecondaryContainerLight = Color(0xFFFFDCC1)
val OnAmberSecondaryContainerLight = Color(0xFF381500)

val BackgroundLight = Color(0xFFFBFBF6)
val SurfaceLight = Color(0xFFFBFBF6)
val SurfaceVariantLight = Color(0xFFEBEAE2)
val OnSurfaceLight = Color(0xFF1B1C19)
val OnSurfaceVariantLight = Color(0xFF48493F)
val OutlineLight = Color(0xFF79796E)
val OutlineVariantLight = Color(0xFFC9C8BC)

val TealPrimaryDark = Color(0xFF8ED9C4)
val OnTealPrimaryDark = Color(0xFF00382E)
val TealPrimaryContainerDark = Color(0xFF005141)
val OnTealPrimaryContainerDark = Color(0xFFAAF2DD)

val AmberSecondaryDark = Color(0xFFFFB77C)
val OnAmberSecondaryDark = Color(0xFF4C2700)
val AmberSecondaryContainerDark = Color(0xFF6D3C00)
val OnAmberSecondaryContainerDark = Color(0xFFFFDCC1)

val BackgroundDark = Color(0xFF14150F)
val SurfaceDark = Color(0xFF14150F)
val SurfaceVariantDark = Color(0xFF48493F)
val OnSurfaceDark = Color(0xFFE4E3D9)
val OnSurfaceVariantDark = Color(0xFFC9C8BC)
val OutlineDark = Color(0xFF929182)
val OutlineVariantDark = Color(0xFF48493F)

// Settlement colors: independent of the primary/secondary roles so they read
// consistently as "money in" / "money out" regardless of theme.
val MoneyIn = Color(0xFF1E8E3E)
val MoneyInContainer = Color(0xFFD7F2DE)
val MoneyOut = Color(0xFFC4432B)
val MoneyOutContainer = Color(0xFFFBE0DA)
