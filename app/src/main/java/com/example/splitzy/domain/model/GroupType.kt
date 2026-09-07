package com.example.splitzy.domain.model

// The one thing that varies by context (running a household, a trip, a
// one-off outing) is how a group is labeled — the underlying "members split
// expenses" mechanics stay identical. HOME covers both shared-household and
// solo/personal tracking; EVENT is for one-off outings (game night, bowling).
enum class GroupType {
    HOME,
    TRIP,
    EVENT
}

// Every new group starts with a few categories already there, matching what
// that kind of group almost always needs — the user can delete any of them.
fun GroupType.defaultCategoryNames(): List<String> = when (this) {
    GroupType.HOME -> listOf("Rent", "Groceries", "Utilities", "Internet", "Repairs")
    GroupType.TRIP -> listOf("Petrol", "Food", "Alcohol", "Stay", "Activities", "Shopping")
    GroupType.EVENT -> listOf("Games", "Food", "Drinks", "Decorations", "Gifts")
}
