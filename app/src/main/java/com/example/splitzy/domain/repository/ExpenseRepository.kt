package com.example.splitzy.domain.repository

import com.example.splitzy.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpensesForGroup(groupId: String): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense): Result<Unit>
    suspend fun syncWithRemote(groupId: String): Result<Unit>
}