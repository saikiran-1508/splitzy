package com.example.splitzy.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.repository.AuthRepository
import com.example.splitzy.domain.usecase.CalculateSpendingSummaryUseCase
import com.example.splitzy.domain.usecase.GetActiveGroupIdUseCase
import com.example.splitzy.domain.usecase.GetAllExpensesUseCase
import com.example.splitzy.domain.usecase.GetGroupsUseCase
import com.example.splitzy.domain.usecase.SetActiveGroupUseCase
import com.example.splitzy.domain.usecase.SpendingSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val email: String? = null,
    val displayName: String? = null,
    val summary: SpendingSummary = SpendingSummary(0.0, 0.0, 0.0, emptyList()),
    val groups: List<Group> = emptyList(),
    val activeGroupId: String? = null,
    val userMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getAllExpenses: GetAllExpensesUseCase,
    getGroups: GetGroupsUseCase,
    getActiveGroupId: GetActiveGroupIdUseCase,
    private val setActiveGroup: SetActiveGroupUseCase,
    private val authRepository: AuthRepository,
    private val calculateSummary: CalculateSpendingSummaryUseCase
) : ViewModel() {

    private val nameChanges = MutableStateFlow(0)
    private val userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileUiState> =
        combine(
            getAllExpenses(),
            getGroups(),
            getActiveGroupId(),
            nameChanges,
            userMessage
        ) { expenses, groups, activeId, _, message ->
            val email = authRepository.currentUserEmail
            ProfileUiState(
                email = email,
                displayName = authRepository.currentUserName,
                summary = calculateSummary(expenses, email),
                groups = groups,
                activeGroupId = activeId ?: groups.lastOrNull()?.id,
                userMessage = message
            )
        }
            .catch { e -> emit(ProfileUiState(userMessage = e.message)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProfileUiState()
            )

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            authRepository.updateDisplayName(name.trim())
                .onSuccess {
                    // Firebase holds the name, not a Flow — nudge the combine
                    // so the screen picks up the new value.
                    nameChanges.value += 1
                    userMessage.value = "Name updated"
                }
                .onFailure { e -> userMessage.value = e.message ?: "Couldn't update name" }
        }
    }

    fun openGroup(groupId: String) = setActiveGroup(groupId)

    fun userMessageShown() {
        userMessage.value = null
    }

    fun signOut() = authRepository.signOut()
}
