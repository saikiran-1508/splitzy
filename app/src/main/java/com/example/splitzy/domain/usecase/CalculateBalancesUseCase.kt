package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Balance
import com.example.splitzy.domain.model.Expense
import javax.inject.Inject

class CalculateBalancesUseCase @Inject constructor() {

    operator fun invoke(expenses: List<Expense>): List<Balance> {
        val netAmount = mutableMapOf<String, Double>()

        for (expense in expenses) {
            val share = expense.amount / expense.splitBetween.size
            netAmount[expense.paidByUserId] =
                (netAmount[expense.paidByUserId] ?: 0.0) + expense.amount
            expense.splitBetween.forEach { userId ->
                netAmount[userId] = (netAmount[userId] ?: 0.0) - share
            }
        }

        return netAmount.map { (userId, amount) -> Balance(userId, amount) }
    }

    // Greedy settlement: fewest transactions to zero everyone out
    fun simplifyDebts(balances: List<Balance>): List<Pair<String, Pair<String, Double>>> {
        val creditors = balances.filter { it.amount > 0.01 }.sortedByDescending { it.amount }.toMutableList()
        val debtors = balances.filter { it.amount < -0.01 }.sortedBy { it.amount }.toMutableList()
        val transactions = mutableListOf<Pair<String, Pair<String, Double>>>()

        var i = 0; var j = 0
        while (i < debtors.size && j < creditors.size) {
            val debtor = debtors[i]; val creditor = creditors[j]
            val settled = minOf(-debtor.amount, creditor.amount)
            transactions.add(debtor.userId to (creditor.userId to settled))

            debtors[i] = debtor.copy(amount = debtor.amount + settled)
            creditors[j] = creditor.copy(amount = creditor.amount - settled)

            if (kotlin.math.abs(debtors[i].amount) < 0.01) i++
            if (kotlin.math.abs(creditors[j].amount) < 0.01) j++
        }
        return transactions
    }
}
