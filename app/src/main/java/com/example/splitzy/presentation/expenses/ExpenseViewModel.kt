package com.example.splitzy.presentation.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.usecase.AddCategoryUseCase
import com.example.splitzy.domain.usecase.AddExpenseUseCase
import com.example.splitzy.domain.usecase.CalculateBalancesUseCase
import com.example.splitzy.domain.usecase.DeleteCategoryUseCase
import com.example.splitzy.domain.usecase.GenerateSettlementReportUseCase
import com.example.splitzy.domain.usecase.GetCategoriesUseCase
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
    getCategories: GetCategoriesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val calculateBalances: CalculateBalancesUseCase,
    private val generateReport: GenerateSettlementReportUseCase
) : ViewModel() {

    private val selectedGroupId = MutableStateFlow<String?>(null)
    private val userMessage = MutableStateFlow<String?>(null)

    // Switches to the new group's Room flow whenever selection changes;
    // the previous group's collection is cancelled automatically.
    private val expenses = selectedGroupId.flatMapLatest { groupId ->
        if (groupId == null) flowOf(emptyList()) else getExpenses(groupId)
    }
    private val categories = selectedGroupId.flatMapLatest { groupId ->
        if (groupId == null) flowOf(emptyList()) else getCategories(groupId)
    }

    val uiState: StateFlow<ExpensesUiState> =
        combine(selectedGroupId, expenses, categories, userMessage) { groupId, expenseList, categoryList, message ->
            val balances = calculateBalances(expenseList)
            ExpensesUiState(
                isLoading = false,
                groupId = groupId,
                expenses = expenseList,
                categories = categoryList,
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
        splitBetween: List<String>,
        categoryId: String?
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
                createdAt = System.currentTimeMillis(),
                categoryId = categoryId
            )
            addExpenseUseCase(expense).onFailure { e ->
                userMessage.value = e.message
            }
            // On success there is nothing to do: Room emits the new list,
            // the combine above recomputes, and the UI updates by itself.
        }
    }

    // Same "no reload needed" pattern as addExpense — Room emits, categories
    // in uiState updates on its own, the just-created one shows up immediately.
    fun addCategory(name: String) {
        val groupId = selectedGroupId.value ?: return
        viewModelScope.launch {
            val category = Category(id = UUID.randomUUID().toString(), groupId = groupId, name = name)
            addCategoryUseCase(category).onFailure { e ->
                userMessage.value = e.message
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category).onFailure { e ->
                userMessage.value = e.message
            }
        }
    }

    fun userMessageShown() {
        userMessage.value = null
    }

    // Formats whatever is currently on screen — callable any time, not tied
    // to any particular event, since the user might want a report mid-trip
    // or after everyone's settled up.
    fun buildReport(groupName: String): String =
        generateReport(groupName, uiState.value.expenses, uiState.value.categories, uiState.value.settlements)
}
