package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Expense>> = repository.getAllExpenses()
}
