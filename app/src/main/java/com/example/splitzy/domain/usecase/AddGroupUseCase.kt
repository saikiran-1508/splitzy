package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.repository.GroupRepository
import javax.inject.Inject

class AddGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(group: Group): Result<Unit> {
        if (group.name.isBlank()) return Result.failure(IllegalArgumentException("Group name can't be empty"))
        if (group.memberIds.size < 2) return Result.failure(IllegalArgumentException("A group needs at least 2 members"))
        return repository.addGroup(group)
    }
}
