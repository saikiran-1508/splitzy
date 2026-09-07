package com.example.splitzy.presentation.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.usecase.AddMemberToGroupUseCase
import com.example.splitzy.domain.usecase.GetCurrentUserEmailUseCase
import com.example.splitzy.domain.usecase.GetGroupByIdUseCase
import com.example.splitzy.domain.usecase.SetActiveGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface JoinState {
    data object Checking : JoinState
    data class Found(val groupName: String, val alreadyMember: Boolean) : JoinState
    data object NotOnThisDevice : JoinState
}

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val getGroupById: GetGroupByIdUseCase,
    private val addMemberToGroup: AddMemberToGroupUseCase,
    private val getCurrentUserEmail: GetCurrentUserEmailUseCase,
    private val setActiveGroup: SetActiveGroupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<JoinState>(JoinState.Checking)
    val state: StateFlow<JoinState> = _state.asStateFlow()

    fun check(groupId: String) {
        viewModelScope.launch {
            val group = getGroupById(groupId).first()
            _state.value = if (group == null) {
                // Groups live only in this device's database — there's no
                // backend to fetch someone else's group from yet.
                JoinState.NotOnThisDevice
            } else {
                JoinState.Found(
                    groupName = group.name,
                    alreadyMember = getCurrentUserEmail() in group.memberIds
                )
            }
        }
    }

    fun join(groupId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            getCurrentUserEmail()?.let { email -> addMemberToGroup(groupId, email) }
            setActiveGroup(groupId)
            onDone()
        }
    }

    fun open(groupId: String, onDone: () -> Unit) {
        setActiveGroup(groupId)
        onDone()
    }
}
