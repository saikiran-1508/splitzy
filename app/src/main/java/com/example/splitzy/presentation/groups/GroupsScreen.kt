package com.example.splitzy.presentation.groups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.model.GroupType
import com.example.splitzy.ui.theme.EventAccent
import com.example.splitzy.ui.theme.HomeAccent
import com.example.splitzy.ui.theme.TripAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onGroupClick: (Group) -> Unit,
    onLogout: () -> Unit,
    viewModel: GroupsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Splitzy", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New group") },
                icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                onClick = { showAddDialog = true }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState.groups.isEmpty() -> EmptyGroupsState(Modifier.fillMaxSize().padding(padding))

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.groups, key = { it.id }) { group ->
                    GroupRow(group = group, onClick = { onGroupClick(group) })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }

    if (showAddDialog) {
        AddGroupDialog(
            onConfirm = { name, members, type ->
                viewModel.addGroup(name, members, type)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun EmptyGroupsState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Default.Groups,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text("No groups yet", style = MaterialTheme.typography.titleMedium)
            Text(
                "A home, a trip, or a one-off night out — create one to start",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GroupRow(group: Group, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InitialsAvatar(text = group.name)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    group.type.icon(),
                    contentDescription = group.type.label(),
                    modifier = Modifier.size(14.dp),
                    tint = group.type.accentColor()
                )
                Text(group.name, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                if (group.memberIds.isEmpty()) "No members yet"
                else group.memberIds.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
internal fun InitialsAvatar(text: String, size: androidx.compose.ui.unit.Dp = 40.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.trim().take(1).uppercase().ifEmpty { "?" },
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun GroupType.icon(): ImageVector = when (this) {
    GroupType.HOME -> Icons.Default.Home
    GroupType.TRIP -> Icons.Default.Luggage
    GroupType.EVENT -> Icons.Default.Celebration
}

private fun GroupType.label(): String = when (this) {
    GroupType.HOME -> "Home"
    GroupType.TRIP -> "Trip"
    GroupType.EVENT -> "Event"
}

private fun GroupType.accentColor(): Color = when (this) {
    GroupType.HOME -> HomeAccent
    GroupType.TRIP -> TripAccent
    GroupType.EVENT -> EventAccent
}

@Composable
private fun AddGroupDialog(
    onConfirm: (name: String, members: List<String>, type: GroupType) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var membersText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(GroupType.HOME) }
    val types = listOf(GroupType.HOME, GroupType.TRIP, GroupType.EVENT)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New group") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    types.forEach { option ->
                        TypeOptionBox(
                            type = option,
                            selected = type == option,
                            onClick = { type = option },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Group name") },
                    placeholder = { Text(type.namePlaceholder()) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = membersText,
                    onValueChange = { membersText = it },
                    label = { Text("Members") },
                    placeholder = { Text("Alex, Sam, Priya") },
                    supportingText = {
                        Text(
                            if (type == GroupType.HOME) "Comma separated — just your own name works for tracking your own spending"
                            else "Comma separated"
                        )
                    },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = { onConfirm(name, membersText.split(","), type) }
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun TypeOptionBox(
    type: GroupType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = type.accentColor()
    Surface(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) accent else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(type.icon(), contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
            Text(
                type.label(),
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

private fun GroupType.namePlaceholder(): String = when (this) {
    GroupType.HOME -> "The Apartment"
    GroupType.TRIP -> "Goa Trip"
    GroupType.EVENT -> "Bowling Night"
}
