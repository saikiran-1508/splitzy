package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(groupId: String): Flow<List<Expense>> =
        repository.getExpensesForGroup(groupId)
}
