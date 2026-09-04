package com.example.splitzy.domain.model

// A "domain" in the user's own words — Food, Rent, Vegetables. User-created,
// scoped to one group, not a fixed enum, since every household's categories differ.
data class Category(
    val id: String,
    val groupId: String,
    val name: String
)
