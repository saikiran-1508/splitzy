package com.example.splitzy.data.repository

import com.example.splitzy.data.local.dao.ExpenseDao
import com.example.splitzy.data.mapper.toDomain
import com.example.splitzy.data.mapper.toDto
import com.example.splitzy.data.mapper.toEntity
import com.example.splitzy.data.remote.SplitzyApiService
import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val apiService: SplitzyApiService
) : ExpenseRepository {

    // UI collects THIS flow. Room emits automatically whenever the table changes —
    // no matter whether the change came from addExpense() below or from syncWithRemote().
    override fun getExpensesForGroup(groupId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesForGroup(groupId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    // Optimistic write: save locally first so the UI updates instantly,
    // then push to the backend. If there's no internet, we don't fail the whole
    // action — Phase 10 (WorkManager) will retry the sync later.
    override suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            expenseDao.insertExpense(expense.toEntity())
            apiService.createExpense(expense.toDto())
            Result.success(Unit)
        } catch (e: IOException) {
            Result.success(Unit) // saved locally, will sync when back online
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Pulls the latest from the server and writes it into Room.
    // The UI never calls this directly — it just sees Room update itself.
    override suspend fun syncWithRemote(groupId: String): Result<Unit> {
        return try {
            val remote = apiService.getExpenses(groupId)
            expenseDao.insertAll(remote.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}