package com.example.splitzy.domain.usecase

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
        settlements: List<Pair<String, Pair<String, Double>>>
    ): String = buildString {
        val today = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date())
        appendLine(groupName)
        appendLine("Splitzy report — $today")
        appendLine()

        appendLine("EXPENSES (${expenses.size})")
        if (expenses.isEmpty()) {
            appendLine("No expenses recorded.")
        } else {
            expenses.sortedBy { it.createdAt }.forEach { expense ->
                appendLine(
                    "- %s: Rs.%.2f, paid by %s, split %d ways".format(
                        expense.description, expense.amount, expense.paidByUserId, expense.splitBetween.size
                    )
                )
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
