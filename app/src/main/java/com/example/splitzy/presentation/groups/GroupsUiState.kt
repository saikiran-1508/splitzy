package com.example.splitzy.presentation.groups

import com.example.splitzy.domain.model.Group

data class GroupsUiState(
    val isLoading: Boolean = true,
    val groups: List<Group> = emptyList(),
    val userMessage: String? = null
)
