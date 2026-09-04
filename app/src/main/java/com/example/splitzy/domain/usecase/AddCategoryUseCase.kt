package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        if (category.name.isBlank()) return Result.failure(IllegalArgumentException("Domain name can't be empty"))
        return repository.addCategory(category)
    }
}
