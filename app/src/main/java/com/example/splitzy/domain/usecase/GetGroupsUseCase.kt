package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.repository.AuthRepository
import com.example.splitzy.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// Only ever returns the signed-in user's own groups. The device database is
// shared by every account that signs in on it, so the filter lives here rather
// than relying on each caller to remember it.
class GetGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<Group>> {
        val ownerId = authRepository.currentUserId ?: return flowOf(emptyList())
        return groupRepository.getGroupsForOwner(ownerId)
    }
}
