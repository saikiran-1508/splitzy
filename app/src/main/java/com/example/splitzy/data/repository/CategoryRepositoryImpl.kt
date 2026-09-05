package com.example.splitzy.data.repository

import com.example.splitzy.data.local.dao.CategoryDao
import com.example.splitzy.data.mapper.toDomain
import com.example.splitzy.data.mapper.toEntity
import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Local-only, same as GroupRepositoryImpl — no backend exists yet to sync against.
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategoriesForGroup(groupId: String): Flow<List<Category>> =
        categoryDao.getCategoriesForGroup(groupId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun addCategory(category: Category): Result<Unit> {
        return try {
            categoryDao.insertCategory(category.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(category: Category): Result<Unit> {
        return try {
            categoryDao.deleteCategory(category.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
