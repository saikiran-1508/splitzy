package com.example.splitzy.domain.repository

import com.example.splitzy.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getGroups(): Flow<List<Group>>
    suspend fun addGroup(group: Group): Result<Unit>
}
