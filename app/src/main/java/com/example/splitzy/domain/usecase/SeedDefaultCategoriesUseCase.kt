package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.model.GroupType
import com.example.splitzy.domain.model.defaultCategoryNames
import com.example.splitzy.domain.repository.CategoryRepository
import java.util.UUID
import javax.inject.Inject

// Runs once, right after a group is created, so a new "The Apartment" group
// isn't an empty shell — it already has Rent/Groceries/Utilities waiting.
// The user can delete any of these; they're a starting point, not a lock-in.
class SeedDefaultCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(groupId: String, type: GroupType) {
        type.defaultCategoryNames().forEach { name ->
            repository.addCategory(Category(id = UUID.randomUUID().toString(), groupId = groupId, name = name))
        }
    }
}
