package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.model.GroupType
import com.example.splitzy.domain.repository.GroupRepository
import javax.inject.Inject

class AddGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(group: Group): Result<Unit> {
        if (group.name.isBlank()) return Result.failure(IllegalArgumentException("Group name can't be empty"))

        // A personal/family tracker can be just you — nobody to split with yet.
        // A trip or shared household needs at least one other person.
        val minMembers = if (group.type == GroupType.PERSONAL_FAMILY) 1 else 2
        if (group.memberIds.size < minMembers) {
            val message = if (minMembers == 1) "Add at least one member"
            else "A group needs at least 2 members"
            return Result.failure(IllegalArgumentException(message))
        }
        return repository.addGroup(group)
    }
}
