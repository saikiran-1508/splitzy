package com.example.splitzy.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // Which group the home screen opens on. Null until the first one is made.
    val activeGroupId: Flow<String?>
    fun setActiveGroupId(groupId: String?)
}
