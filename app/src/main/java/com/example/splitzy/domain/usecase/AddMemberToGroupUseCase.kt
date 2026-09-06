package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.repository.GroupRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Members aren't fixed at creation — someone who joined the trip late can be
// added afterwards without touching the expenses already recorded.
class AddMemberToGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, member: String): Result<Unit> {
        val trimmed = member.trim()
        if (trimmed.isBlank()) return Result.failure(IllegalArgumentException("Enter a name or email"))

        val group = repository.getGroupById(groupId).first()
            ?: return Result.failure(IllegalStateException("Group not found"))
        if (trimmed in group.memberIds) {
            return Result.failure(IllegalArgumentException("$trimmed is already in this group"))
        }
        return repository.updateGroup(group.copy(memberIds = group.memberIds + trimmed))
    }
}
