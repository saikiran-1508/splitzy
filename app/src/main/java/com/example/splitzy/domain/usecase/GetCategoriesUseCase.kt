package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(groupId: String): Flow<List<Category>> = repository.getCategoriesForGroup(groupId)
}
