package com.example.splitzy.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Categories are free text the user types (Food, Petrol, Vegetables, ...),
// not a fixed enum — so instead of hardcoding "Food is red", common names
// get a recognizable icon by keyword, and every name (known or not) gets a
// color it keeps consistently, chosen deterministically from its own text so
// the same category always looks the same without storing a color anywhere.

data class CategoryStyle(val color: Color, val icon: ImageVector)

private val CategoryPalette = listOf(
    Color(0xFFE8604A), // coral
    Color(0xFF3D8BCF), // blue
    Color(0xFF9B6FC7), // purple
    Color(0xFF5C6FBF), // indigo
    Color(0xFFC77DD1), // violet
    Color(0xFFE86BA0), // rose
    Color(0xFFD99A2B), // amber
    Color(0xFF2FA37A), // teal
)

private val KnownCategoryIcons = listOf(
    listOf("food", "dinner", "lunch", "breakfast", "restaurant") to Icons.Default.Restaurant,
    listOf("petrol", "fuel", "gas") to Icons.Default.LocalGasStation,
    listOf("alcohol", "drinks", "bar") to Icons.Default.LocalBar,
    listOf("rent", "stay", "hotel", "room") to Icons.Default.Hotel,
    listOf("utilities", "electricity", "wifi", "internet") to Icons.Default.Bolt,
    listOf("groceries", "vegetables") to Icons.Default.ShoppingCart,
    listOf("shopping") to Icons.Default.ShoppingBag,
    listOf("games", "activities", "entertainment", "bowling") to Icons.Default.ConfirmationNumber
)

fun categoryStyleFor(name: String): CategoryStyle {
    val key = name.trim().lowercase()
    val icon = KnownCategoryIcons.firstOrNull { (keywords, _) -> keywords.any { key.contains(it) } }?.second
        ?: Icons.Default.Sell
    val color = CategoryPalette[(key.hashCode().and(Int.MAX_VALUE)) % CategoryPalette.size]
    return CategoryStyle(color, icon)
}

// Same idea, for per-person avatar colors (settlements, expense rows) —
// every name gets a stable color without anywhere to store one.
fun avatarColorFor(name: String): Color {
    val key = name.trim().lowercase()
    return CategoryPalette[(key.hashCode().and(Int.MAX_VALUE)) % CategoryPalette.size]
}
