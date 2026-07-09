package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> {
        if (expense.amount <= 0) return Result.failure(IllegalArgumentException("Amount must be positive"))
        if (expense.splitBetween.isEmpty()) return Result.failure(IllegalArgumentException("Must split between at least one person"))
        return repository.addExpense(expense)
    }
}