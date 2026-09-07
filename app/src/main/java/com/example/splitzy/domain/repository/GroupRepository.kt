package com.example.splitzy.domain.repository

import com.example.splitzy.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getGroupsForOwner(ownerId: String): Flow<List<Group>>
    fun getGroupById(groupId: String): Flow<Group?>
    suspend fun addGroup(group: Group): Result<Unit>
    suspend fun updateGroup(group: Group): Result<Unit>
}
