package com.example.splitzy.domain.model

data class Expense(
    val id: String,
    val groupId: String,
    val description: String,
    val amount: Double,
    val paidByUserId: String,
    val splitBetween: List<String>,   // user IDs sharing this expense
    val createdAt: Long,
    val categoryId: String? = null    // which "domain" (Food, Rent, ...) this belongs to, if any
)

data class Group(
    val id: String,
    val name: String,
    val memberIds: List<String>,
    val type: GroupType = GroupType.TRIP,
    // Who created it. Room is shared by every account that signs in on this
    // device, so groups are filtered by this rather than handed to whoever
    // happens to log in next.
    val ownerId: String = ""
)

data class Balance(
    val userId: String,
    val amount: Double   // positive = owed to them, negative = they owe
)