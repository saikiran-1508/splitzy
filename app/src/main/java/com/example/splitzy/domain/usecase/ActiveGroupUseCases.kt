package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveGroupIdUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<String?> = repository.activeGroupId
}

class SetActiveGroupUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(groupId: String?) = repository.setActiveGroupId(groupId)
}
