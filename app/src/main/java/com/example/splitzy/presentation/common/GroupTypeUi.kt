package com.example.splitzy.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.splitzy.domain.model.GroupType
import com.example.splitzy.ui.theme.SplitzyTheme

// One place for how a group type looks and reads, shared by the groups list,
// the create-group screen and the group header.

fun GroupType.icon(): ImageVector = when (this) {
    GroupType.HOME -> Icons.Default.Home
    GroupType.TRIP -> Icons.Default.Luggage
    GroupType.EVENT -> Icons.Default.CardGiftcard
}

fun GroupType.label(): String = when (this) {
    GroupType.HOME -> "Home"
    GroupType.TRIP -> "Trip"
    GroupType.EVENT -> "Event"
}

fun GroupType.description(): String = when (this) {
    GroupType.HOME -> "Shared household or personal tracking"
    GroupType.TRIP -> "Vacations, travel and adventures"
    GroupType.EVENT -> "Parties, celebrations and special events"
}

fun GroupType.memberRule(): String = when (this) {
    GroupType.HOME -> "1+ members"
    else -> "2+ members"
}

fun GroupType.namePlaceholder(): String = when (this) {
    GroupType.HOME -> "Home Sweet Home"
    GroupType.TRIP -> "Goa Trip 2025"
    GroupType.EVENT -> "Birthday Bash"
}

@Composable
fun GroupType.accent(): Color = when (this) {
    GroupType.HOME -> SplitzyTheme.accents.homeAccent
    GroupType.TRIP -> SplitzyTheme.accents.tripAccent
    GroupType.EVENT -> SplitzyTheme.accents.eventAccent
}

@Composable
fun GroupType.container(): Color = when (this) {
    GroupType.HOME -> SplitzyTheme.accents.homeContainer
    GroupType.TRIP -> SplitzyTheme.accents.tripContainer
    GroupType.EVENT -> SplitzyTheme.accents.eventContainer
}
