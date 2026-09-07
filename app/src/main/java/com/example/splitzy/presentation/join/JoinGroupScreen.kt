package com.example.splitzy.presentation.join

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Landing screen for splitzy://join/<groupId> links.
@Composable
fun JoinGroupScreen(
    groupId: String,
    onDone: () -> Unit,
    viewModel: JoinGroupViewModel = hiltViewModel()
) {
    LaunchedEffect(groupId) { viewModel.check(groupId) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                JoinState.Checking -> CircularProgressIndicator()

                is JoinState.Found -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        s.groupName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        if (s.alreadyMember) "You're already in this group."
                        else "You've been invited to split expenses here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            if (s.alreadyMember) viewModel.open(groupId, onDone)
                            else viewModel.join(groupId, onDone)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    ) {
                        Text(
                            if (s.alreadyMember) "Open group" else "Join group",
                            modifier = Modifier.padding(vertical = 6.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                JoinState.NotOnThisDevice -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "This group isn't on this device",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Splitzy stores groups locally, so an invite only opens a " +
                            "group that already exists on the phone you tapped it on. " +
                            "Syncing groups between devices needs the backend, which " +
                            "isn't built yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(
                        onClick = onDone,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(top = 12.dp)
                    ) { Text("Go to my groups") }
                }
            }
        }
    }
}
