package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Expense
import java.util.Calendar
import javax.inject.Inject

data class MonthlySpend(
    val year: Int,
    val month: Int, // Calendar.MONTH, 0-based
    val label: String, // e.g. "Sep 2026"
    val amount: Double
)

data class SpendingSummary(
    val thisMonth: Double,
    val thisYear: Double,
    val allTime: Double,
    val recentMonths: List<MonthlySpend>
)

// "How much have I actually spent" means this user's share of each expense
// they were part of — not the full bill they happened to pay for the table.
class CalculateSpendingSummaryUseCase @Inject constructor() {

    operator fun invoke(
        expenses: List<Expense>,
        userId: String?,
        now: Long = System.currentTimeMillis()
    ): SpendingSummary {
        if (userId == null) return SpendingSummary(0.0, 0.0, 0.0, emptyList())

        val calendar = Calendar.getInstance()
        val currentYear = calendar.apply { timeInMillis = now }.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val shares = expenses.mapNotNull { expense ->
            if (userId !in expense.splitBetween || expense.splitBetween.isEmpty()) return@mapNotNull null
            val share = expense.amount / expense.splitBetween.size
            val cal = Calendar.getInstance().apply { timeInMillis = expense.createdAt }
            Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), share)
        }

        val byMonth = shares
            .groupBy { it.first to it.second }
            .map { (key, entries) ->
                val (year, month) = key
                MonthlySpend(
                    year = year,
                    month = month,
                    label = "${MONTH_NAMES[month]} $year",
                    amount = entries.sumOf { it.third }
                )
            }
            .sortedWith(compareByDescending<MonthlySpend> { it.year }.thenByDescending { it.month })

        return SpendingSummary(
            thisMonth = shares.filter { it.first == currentYear && it.second == currentMonth }.sumOf { it.third },
            thisYear = shares.filter { it.first == currentYear }.sumOf { it.third },
            allTime = shares.sumOf { it.third },
            recentMonths = byMonth.take(6)
        )
    }
}

private val MONTH_NAMES = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)
