package com.example.splitzy.data.mapper

import com.example.splitzy.data.local.entity.ExpenseEntity
import com.example.splitzy.data.remote.dto.ExpenseDto
import com.example.splitzy.domain.model.Expense

fun ExpenseEntity.toDomain() = Expense(
    id = id, groupId = groupId, description = description, amount = amount,
    paidByUserId = paidByUserId, splitBetween = splitBetween, createdAt = createdAt
)

fun Expense.toEntity() = ExpenseEntity(
    id = id, groupId = groupId, description = description, amount = amount,
    paidByUserId = paidByUserId, splitBetween = splitBetween, createdAt = createdAt
)

fun ExpenseDto.toDomain() = Expense(
    id = id, groupId = groupId, description = description, amount = amount,
    paidByUserId = paidByUserId, splitBetween = splitBetween, createdAt = createdAt
)

fun ExpenseDto.toEntity() = ExpenseEntity(
    id = id, groupId = groupId, description = description, amount = amount,
    paidByUserId = paidByUserId, splitBetween = splitBetween, createdAt = createdAt
)

fun Expense.toDto() = ExpenseDto(
    id = id, groupId = groupId, description = description, amount = amount,
    paidByUserId = paidByUserId, splitBetween = splitBetween, createdAt = createdAt
)