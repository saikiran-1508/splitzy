package com.example.splitzy.data.repository

import com.example.splitzy.data.local.dao.GroupDao
import com.example.splitzy.data.mapper.toDomain
import com.example.splitzy.data.mapper.toEntity
import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Local-only for now: the backend has no group endpoints yet.
// When it does, this follows the same offline-first shape as ExpenseRepositoryImpl.
class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao
) : GroupRepository {

    override fun getGroups(): Flow<List<Group>> =
        groupDao.getAllGroups().map { entities -> entities.map { it.toDomain() } }

    override fun getGroupById(groupId: String): Flow<Group?> =
        groupDao.getGroupById(groupId).map { entity -> entity?.toDomain() }

    override suspend fun addGroup(group: Group): Result<Unit> {
        return try {
            groupDao.insertGroup(group.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Same insert — the DAO replaces on matching id, so saving an edited
    // group (a member added later, say) updates the existing row.
    override suspend fun updateGroup(group: Group): Result<Unit> {
        return try {
            groupDao.insertGroup(group.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
