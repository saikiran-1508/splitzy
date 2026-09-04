package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// Pure text formatting, no Android imports — the ViewModel hands this to a
// share intent, but nothing here knows that's where it's going.
class GenerateSettlementReportUseCase @Inject constructor() {

    operator fun invoke(
        groupName: String,
        expenses: List<Expense>,
        categories: List<Category>,
        settlements: List<Pair<String, Pair<String, Double>>>
    ): String = buildString {
        val today = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date())
        val categoryNames = categories.associateBy { it.id }

        appendLine(groupName)
        appendLine("Splitzy report — $today")
        appendLine()

        appendLine("EXPENSES (${expenses.size})")
        if (expenses.isEmpty()) {
            appendLine("No expenses recorded.")
        } else {
            expenses.sortedBy { it.createdAt }.forEach { expense ->
                val category = expense.categoryId?.let { categoryNames[it]?.name }
                appendLine(
                    "- %s%s: Rs.%.2f, paid by %s, split %d ways".format(
                        expense.description,
                        if (category != null) " [$category]" else "",
                        expense.amount, expense.paidByUserId, expense.splitBetween.size
                    )
                )
            }

            val byCategory = expenses.groupBy { it.categoryId?.let { id -> categoryNames[id]?.name } ?: "Uncategorized" }
            if (byCategory.size > 1 || byCategory.keys.singleOrNull() != "Uncategorized") {
                appendLine()
                appendLine("BY DOMAIN")
                byCategory.entries.sortedByDescending { it.value.sumOf { e -> e.amount } }.forEach { (name, group) ->
                    appendLine("- %s: Rs.%.2f".format(name, group.sumOf { it.amount }))
                }
            }
        }

        appendLine()
        appendLine("SETTLEMENTS")
        if (settlements.isEmpty()) {
            appendLine("Everyone is settled up.")
        } else {
            settlements.forEach { (debtor, creditorAndAmount) ->
                val (creditor, amount) = creditorAndAmount
                appendLine("- %s owes %s Rs.%.2f".format(debtor, creditor, amount))
            }
        }
    }
}
