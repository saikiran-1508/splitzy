package com.example.splitzy.presentation.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.splitzy.R
import com.example.splitzy.domain.model.Group
import com.example.splitzy.domain.model.GroupType
import com.example.splitzy.ui.theme.EventAccent
import com.example.splitzy.ui.theme.HomeAccent
import com.example.splitzy.ui.theme.TripAccent
import com.example.splitzy.ui.theme.avatarColorFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onGroupClick: (Group) -> Unit,
    onLogout: () -> Unit,
    viewModel: GroupsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var creationStep by remember { mutableStateOf<GroupCreationStep?>(null) }

    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(
                            painter = painterResource(R.drawable.splitzy_logo),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Text("Splitzy", fontWeight = FontWeight.ExtraBold)
                    }
                },
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
                onClick = { creationStep = GroupCreationStep.TypeAndName },
                shape = RoundedCornerShape(50)
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.groups, key = { it.id }) { group ->
                    GroupCard(group = group, onClick = { onGroupClick(group) })
                }
            }
        }
    }

    when (val step = creationStep) {
        GroupCreationStep.TypeAndName -> {
            AddGroupDialog(
                onConfirm = { name, type -> creationStep = GroupCreationStep.Members(name, type) },
                onDismiss = { creationStep = null }
            )
        }
        is GroupCreationStep.Members -> {
            AddMembersDialog(
                ownerEmail = viewModel.currentUserEmail(),
                onConfirm = { members ->
                    viewModel.addGroup(step.name, members, step.type)
                    creationStep = null
                },
                onDismiss = { creationStep = null }
            )
        }
        null -> {}
    }
}

// Creating a group is two steps, matching the reference flow: pick a type
// and name it, then add who's in it (you're always first, as Owner).
private sealed class GroupCreationStep {
    object TypeAndName : GroupCreationStep()
    data class Members(val name: String, val type: GroupType) : GroupCreationStep()
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

// A solid-color card per group, tinted by its type — this is the one place
// group type should be unmissable at a glance, not a subtle badge.
@Composable
private fun GroupCard(group: Group, onClick: () -> Unit) {
    val accent = group.type.accentColor()
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = accent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(group.type.icon(), contentDescription = group.type.label(), tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "${group.type.label()} · ${group.memberIds.size} member${if (group.memberIds.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

internal fun GroupType.icon(): ImageVector = when (this) {
    GroupType.HOME -> Icons.Default.Home
    GroupType.TRIP -> Icons.Default.Luggage
    GroupType.EVENT -> Icons.Default.Celebration
}

internal fun GroupType.label(): String = when (this) {
    GroupType.HOME -> "Home"
    GroupType.TRIP -> "Trip"
    GroupType.EVENT -> "Event"
}

private fun GroupType.subtitle(): String = when (this) {
    GroupType.HOME -> "Shared household or personal tracking"
    GroupType.TRIP -> "Vacations, travel with friends"
    GroupType.EVENT -> "Parties, celebrations, special events"
}

private fun GroupType.memberHint(): String = when (this) {
    GroupType.HOME -> "Just add yourself"
    else -> "Minimum 2 members needed"
}

internal fun GroupType.accentColor(): Color = when (this) {
    GroupType.HOME -> HomeAccent
    GroupType.TRIP -> TripAccent
    GroupType.EVENT -> EventAccent
}

@Composable
private fun AddGroupDialog(
    onConfirm: (name: String, type: GroupType) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(GroupType.HOME) }
    val types = listOf(GroupType.HOME, GroupType.TRIP, GroupType.EVENT)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create a New Group") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Choose the type of group",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                types.forEach { option ->
                    TypeOptionRow(
                        type = option,
                        selected = type == option,
                        onClick = { type = option }
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Group name") },
                    placeholder = { Text(type.namePlaceholder()) },
                    singleLine = true,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(50),
                onClick = { onConfirm(name, type) }
            ) { Text("Next") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun TypeOptionRow(
    type: GroupType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = type.accentColor()
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) accent else accent.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (selected) Color.White.copy(alpha = 0.22f) else accent.copy(alpha = 0.18f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    type.icon(),
                    contentDescription = null,
                    tint = if (selected) Color.White else accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    type.label(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    type.subtitle(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                type.memberHint(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun GroupType.namePlaceholder(): String = when (this) {
    GroupType.HOME -> "The Apartment"
    GroupType.TRIP -> "Goa Trip"
    GroupType.EVENT -> "Bowling Night"
}

@Composable
private fun AddMembersDialog(
    ownerEmail: String?,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var newMemberText by remember { mutableStateOf("") }
    var members by remember { mutableStateOf(listOfNotNull(ownerEmail)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Members") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newMemberText,
                        onValueChange = { newMemberText = it },
                        label = { Text("Search or enter email") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        enabled = newMemberText.isNotBlank(),
                        onClick = {
                            val email = newMemberText.trim()
                            if (email.isNotEmpty() && email !in members) members = members + email
                            newMemberText = ""
                        }
                    ) { Text("Add") }
                }
                members.forEach { member ->
                    val isOwner = member == ownerEmail
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp).background(avatarColorFor(member), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                member.trim().take(1).uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Text(
                            if (isOwner) "You ($member)" else member,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (isOwner) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    "Owner",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        } else {
                            IconButton(onClick = { members = members - member }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove $member")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(shape = RoundedCornerShape(50), onClick = { onConfirm(members) }) { Text("Create Group") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Back") }
        }
    )
}
