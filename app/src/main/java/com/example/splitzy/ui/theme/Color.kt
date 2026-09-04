package com.example.splitzy.ui.theme

import androidx.compose.ui.graphics.Color

// A "home" palette: muted sage green + clay terracotta on a warm cream base.
// Deliberately not a saturated brand color and not stark black/white —
// closer to the colors of an actual living room than a fintech dashboard.

val SagePrimaryLight = Color(0xFF4F7452)
val OnSagePrimaryLight = Color(0xFFFFFFFF)
val SagePrimaryContainerLight = Color(0xFFDCEBDC)
val OnSagePrimaryContainerLight = Color(0xFF17291B)

val ClaySecondaryLight = Color(0xFFB56A4B)
val OnClaySecondaryLight = Color(0xFFFFFFFF)
val ClaySecondaryContainerLight = Color(0xFFF4DED2)
val OnClaySecondaryContainerLight = Color(0xFF3A1D0F)

val BackgroundLight = Color(0xFFF7F2EA)
val SurfaceLight = Color(0xFFFBF8F2)
val SurfaceVariantLight = Color(0xFFEAE2D4)
val OnSurfaceLight = Color(0xFF2B2620)
val OnSurfaceVariantLight = Color(0xFF5B5546)
val OutlineLight = Color(0xFF857E6C)
val OutlineVariantLight = Color(0xFFD8D0BF)

val SagePrimaryDark = Color(0xFFA0C6A2)
val OnSagePrimaryDark = Color(0xFF17291B)
val SagePrimaryContainerDark = Color(0xFF3E5940)
val OnSagePrimaryContainerDark = Color(0xFFDCEBDC)

val ClaySecondaryDark = Color(0xFFE3A484)
val OnClaySecondaryDark = Color(0xFF3A1D0F)
val ClaySecondaryContainerDark = Color(0xFF6B4531)
val OnClaySecondaryContainerDark = Color(0xFFF4DED2)

// Warm charcoal instead of pure black — reads as dim room lighting, not "OLED off".
val BackgroundDark = Color(0xFF201C16)
val SurfaceDark = Color(0xFF26221B)
val SurfaceVariantDark = Color(0xFF4A4436)
val OnSurfaceDark = Color(0xFFECE6D9)
val OnSurfaceVariantDark = Color(0xFFD2CAB8)
val OutlineDark = Color(0xFF9C9585)
val OutlineVariantDark = Color(0xFF4A4436)

// Settlement colors: independent of primary/secondary so "money in / money out"
// reads consistently regardless of theme.
val MoneyIn = Color(0xFF3F8455)
val MoneyInContainer = Color(0xFFDCEBDC)
val MoneyOut = Color(0xFFB5473A)
val MoneyOutContainer = Color(0xFFF6DFD9)

// Small accent tints for group-type badges — legible on both light and dark
// surfaces since they're only ever used at icon scale, not as fills.
val TripAccent = Color(0xFFC1694F)
val HouseholdAccent = Color(0xFF5B8A63)
val PersonalFamilyAccent = Color(0xFF8E6B93)
