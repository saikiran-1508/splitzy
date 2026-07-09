package com.example.splitzy.domain.model

data class Expense(
    val id: String,
    val groupId: String,
    val description: String,
    val amount: Double,
    val paidByUserId: String,
    val splitBetween: List<String>,   // user IDs sharing this expense
    val createdAt: Long
)

data class Group(
    val id: String,
    val name: String,
    val memberIds: List<String>
)

data class Balance(
    val userId: String,
    val amount: Double   // positive = owed to them, negative = they owe
)