package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> = repository.deleteCategory(category)
}
