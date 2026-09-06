package com.example.splitzy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.usecase.GetActiveGroupIdUseCase
import com.example.splitzy.domain.usecase.GetGroupsUseCase
import com.example.splitzy.domain.usecase.SetActiveGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeGroup: Group? = null,
    val hasAnyGroup: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getGroups: GetGroupsUseCase,
    getActiveGroupId: GetActiveGroupIdUseCase,
    private val setActiveGroup: SetActiveGroupUseCase
) : ViewModel() {

    // If the stored active group was deleted (or never set), fall back to the
    // most recent one so home isn't stuck empty while groups exist.
    val uiState: StateFlow<HomeUiState> =
        combine(getGroups(), getActiveGroupId()) { groups, activeId ->
            val active = groups.firstOrNull { it.id == activeId } ?: groups.lastOrNull()
            HomeUiState(
                isLoading = false,
                activeGroup = active,
                hasAnyGroup = groups.isNotEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun selectGroup(groupId: String) = setActiveGroup(groupId)
}
