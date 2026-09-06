package com.example.splitzy.presentation.expenses

import com.example.splitzy.domain.model.Balance
import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.model.Group

// Everything the expenses screen needs to draw itself, in one immutable snapshot.
// The screen never computes anything — it just renders whatever this says.
data class ExpensesUiState(
    val isLoading: Boolean = true,
    val groupId: String? = null,
    val group: Group? = null,
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val balances: List<Balance> = emptyList(),
    // debtor -> (creditor -> amount), straight from CalculateBalancesUseCase.simplifyDebts
    val settlements: List<Pair<String, Pair<String, Double>>> = emptyList(),
    // one-shot message (e.g. validation failure) — screen shows it, then calls userMessageShown()
    val userMessage: String? = null
)
