package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupsUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    operator fun invoke(): Flow<List<Group>> = repository.getGroups()
}
