package com.example.splitzy.data.local

import android.content.Context
import com.example.splitzy.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// One string doesn't justify a Room table or a DataStore dependency. The
// StateFlow is what the UI observes; SharedPreferences is just where it
// survives a restart.
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SettingsRepository {

    private val prefs = context.getSharedPreferences("splitzy_settings", Context.MODE_PRIVATE)
    private val _activeGroupId = MutableStateFlow(prefs.getString(KEY_ACTIVE_GROUP, null))

    override val activeGroupId: Flow<String?> = _activeGroupId.asStateFlow()

    override fun setActiveGroupId(groupId: String?) {
        prefs.edit().putString(KEY_ACTIVE_GROUP, groupId).apply()
        _activeGroupId.value = groupId
    }

    private companion object {
        const val KEY_ACTIVE_GROUP = "active_group_id"
    }
}
