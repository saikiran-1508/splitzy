package com.example.splitzy.domain.repository

import com.example.splitzy.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategoriesForGroup(groupId: String): Flow<List<Category>>
    suspend fun addCategory(category: Category): Result<Unit>
}
