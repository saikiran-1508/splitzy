package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupByIdUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    operator fun invoke(groupId: String): Flow<Group?> = repository.getGroupById(groupId)
}
