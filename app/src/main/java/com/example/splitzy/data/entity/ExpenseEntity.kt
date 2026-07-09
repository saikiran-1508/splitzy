package com.example.splitzy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.splitzy.data.local.Converters

@Entity(tableName = "expenses")
@TypeConverters(Converters::class)
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val description: String,
    val amount: Double,
    val paidByUserId: String,
    val splitBetween: List<String>,
    val createdAt: Long
)