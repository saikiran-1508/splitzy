package com.example.splitzy.presentation.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.usecase.AddGroupUseCase
import com.example.splitzy.domain.usecase.GetGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    getGroups: GetGroupsUseCase,
    private val addGroupUseCase: AddGroupUseCase
) : ViewModel() {

    private val userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<GroupsUiState> =
        combine(getGroups(), userMessage) { groups, message ->
            GroupsUiState(isLoading = false, groups = groups, userMessage = message)
        }
            .catch { e -> emit(GroupsUiState(isLoading = false, userMessage = e.message)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GroupsUiState()
            )

    // No user accounts until Phase 8 (Firebase Auth), so member names double as IDs.
    fun addGroup(name: String, memberNames: List<String>) {
        viewModelScope.launch {
            val group = Group(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                memberIds = memberNames.map { it.trim() }.filter { it.isNotEmpty() }
            )
            addGroupUseCase(group).onFailure { e ->
                userMessage.value = e.message
            }
        }
    }

    fun userMessageShown() {
        userMessage.value = null
    }
}
