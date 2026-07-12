package com.example.splitzy.presentation.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.usecase.AddExpenseUseCase
import com.example.splitzy.domain.usecase.CalculateBalancesUseCase
import com.example.splitzy.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    getExpenses: GetExpensesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val calculateBalances: CalculateBalancesUseCase
) : ViewModel() {

    private val selectedGroupId = MutableStateFlow<String?>(null)
    private val userMessage = MutableStateFlow<String?>(null)

    // Switches to the new group's Room flow whenever selection changes;
    // the previous group's collection is cancelled automatically.
    private val expenses = selectedGroupId.flatMapLatest { groupId ->
        if (groupId == null) flowOf(emptyList()) else getExpenses(groupId)
    }

    val uiState: StateFlow<ExpensesUiState> =
        combine(selectedGroupId, expenses, userMessage) { groupId, expenseList, message ->
            val balances = calculateBalances(expenseList)
            ExpensesUiState(
                isLoading = false,
                groupId = groupId,
                expenses = expenseList,
                balances = balances,
                settlements = calculateBalances.simplifyDebts(balances),
                userMessage = message
            )
        }
            .catch { e -> emit(ExpensesUiState(isLoading = false, userMessage = e.message)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ExpensesUiState()
            )

    fun selectGroup(groupId: String) {
        selectedGroupId.value = groupId
    }

    fun addExpense(
        description: String,
        amount: Double,
        paidByUserId: String,
        splitBetween: List<String>
    ) {
        val groupId = selectedGroupId.value ?: return
        viewModelScope.launch {
            val expense = Expense(
                id = UUID.randomUUID().toString(),
                groupId = groupId,
                description = description,
                amount = amount,
                paidByUserId = paidByUserId,
                splitBetween = splitBetween,
                createdAt = System.currentTimeMillis()
            )
            addExpenseUseCase(expense).onFailure { e ->
                userMessage.value = e.message
            }
            // On success there is nothing to do: Room emits the new list,
            // the combine above recomputes, and the UI updates by itself.
        }
    }

    fun userMessageShown() {
        userMessage.value = null
    }
}
