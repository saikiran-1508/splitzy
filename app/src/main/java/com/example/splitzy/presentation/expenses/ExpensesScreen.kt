package com.example.splitzy.presentation.expenses

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.splitzy.domain.model.Balance
import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.model.Expense
import com.example.splitzy.domain.model.Group
import com.example.splitzy.presentation.common.accent
import com.example.splitzy.presentation.common.icon
import com.example.splitzy.presentation.common.label
import com.example.splitzy.presentation.common.rememberContactEmailPicker
import com.example.splitzy.ui.theme.SplitzyTheme
import com.example.splitzy.ui.theme.avatarColorFor
import com.example.splitzy.ui.theme.categoryStyleFor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Shown as the home screen for the active group. onBack is null there (nothing
// to go back to); onProfileClick is null when it's pushed as its own route.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetail(
    groupId: String,
    groupName: String,
    onBack: (() -> Unit)?,
    onProfileClick: (() -> Unit)?,
    onAddExpense: (String) -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    LaunchedEffect(groupId) { viewModel.selectGroup(groupId) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(0) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentUser = remember { viewModel.currentUserEmail() }
    val group = uiState.group
    val members = group?.memberIds ?: emptyList()

    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { GroupHeaderTitle(group = group, fallbackName = groupName) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onAddExpense(groupId) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add expense")
                    }
                    if (onProfileClick != null) {
                        IconButton(onClick = onProfileClick) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    text = { Text("Add Expense", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    onClick = { onAddExpense(groupId) },
                    shape = RoundedCornerShape(18.dp)
                )
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Expenses") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Members") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Settle Up") })
                }
                when (selectedTab) {
                    0 -> ExpensesTab(
                        expenses = uiState.expenses,
                        categories = uiState.categories,
                        currentUser = currentUser,
                        modifier = Modifier.weight(1f)
                    )
                    1 -> MembersTab(
                        members = members,
                        currentUser = currentUser,
                        onAddMember = { showAddMemberDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    else -> SettleUpTab(
                        settlements = uiState.settlements,
                        balances = uiState.balances,
                        currentUser = currentUser,
                        onShare = {
                            val report = viewModel.buildReport(groupName)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "$groupName — Splitzy report")
                                putExtra(Intent.EXTRA_TEXT, report)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share report"))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            onConfirm = { viewModel.addMember(it) },
            onDismiss = { showAddMemberDialog = false }
        )
    }
}

@Composable
private fun AddMemberDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add member") },
        text = {
            Column {
                Text(
                    "Someone who joined late can still be added — expenses already recorded stay as they are.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Name or email") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                val pickFromContacts = rememberContactEmailPicker { email = it }
                OutlinedButton(
                    onClick = pickFromContacts,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Icon(Icons.Default.Contacts, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Pick from contacts", modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        confirmButton = {
            Button(
                enabled = email.isNotBlank(),
                shape = RoundedCornerShape(50),
                onClick = { onConfirm(email.trim()); onDismiss() }
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun GroupHeaderTitle(group: Group?, fallbackName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (group != null) {
            Box(
                modifier = Modifier.size(34.dp).background(group.type.accent(), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    group.type.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column {
            Text(
                group?.name ?: fallbackName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            if (group != null) {
                Text(
                    "${group.type.label()} · ${group.memberIds.size} member${if (group.memberIds.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ExpensesTab(
    expenses: List<Expense>,
    categories: List<Category>,
    currentUser: String?,
    modifier: Modifier = Modifier
) {
    if (expenses.isEmpty()) {
        EmptyTabState("No expenses yet", "Tap Add Expense to log the first one", modifier)
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(expenses, key = { it.id }) { expense ->
            val categoryName = categories.find { it.id == expense.categoryId }?.name
            ExpenseCard(expense, categoryName, currentUser)
        }
    }
}

@Composable
private fun ExpenseCard(expense: Expense, categoryName: String?, currentUser: String?) {
    val style = categoryStyleFor(categoryName ?: expense.description)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(style.color, RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(style.icon, contentDescription = categoryName, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    expense.description,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    "${payerLabel(expense.paidByUserId, currentUser)} · ${expense.createdAt.asShortDate()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                expense.amount.money(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MembersTab(
    members: List<String>,
    currentUser: String?,
    onAddMember: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(members) { index, member ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                NameAvatar(member, size = 44.dp)
                Column(Modifier.weight(1f)) {
                    Text(
                        if (member == currentUser) "You" else member.substringBefore("@"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        member,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                RolePill(isOwner = index == 0)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
        item {
            OutlinedButton(
                onClick = onAddMember,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Add member", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun RolePill(isOwner: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isOwner) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            if (isOwner) "Owner" else "Member",
            style = MaterialTheme.typography.labelLarge,
            color = if (isOwner) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SettleUpTab(
    settlements: List<Pair<String, Pair<String, Double>>>,
    balances: List<Balance>,
    currentUser: String?,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Text(
            "Clear your group balances",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        if (settlements.isEmpty() && balances.all { kotlin.math.abs(it.amount) < 0.01 }) {
            EmptyTabState("Everyone is settled up", "No payments needed right now", Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (settlements.isNotEmpty()) {
                    item { SettleSectionLabel("Who pays whom") }
                    items(settlements) { (debtor, creditorAndAmount) ->
                        val (creditor, amount) = creditorAndAmount
                        SettlementCard(debtor, creditor, amount, currentUser)
                    }
                }
                // The per-person view: what each member is up or down overall,
                // before it's simplified into the fewest transfers above.
                item { SettleSectionLabel("Each person's balance") }
                items(balances.sortedByDescending { it.amount }) { balance ->
                    BalanceRow(balance, currentUser)
                }
            }
        }
        Button(
            onClick = onShare,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Share Settlement Summary", modifier = Modifier.padding(vertical = 6.dp), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettlementCard(debtor: String, creditor: String, amount: Double, currentUser: String?) {
    // Money coming to you reads green; money you owe reads coral.
    val youAreOwed = creditor == currentUser
    val youOwe = debtor == currentUser
    val amountColor = when {
        youAreOwed -> SplitzyTheme.accents.moneyIn
        youOwe -> SplitzyTheme.accents.moneyOut
        else -> MaterialTheme.colorScheme.onSurface
    }
    val relation = when {
        youAreOwed -> "owes you"
        youOwe -> "you owe ${creditor.substringBefore("@")}"
        else -> "owes ${creditor.substringBefore("@")}"
    }
    val personShown = if (youOwe) creditor else debtor

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NameAvatar(personShown, size = 44.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    if (youOwe) "You" else personShown.substringBefore("@"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    relation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                amount.money(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
private fun SettleSectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
private fun BalanceRow(balance: Balance, currentUser: String?) {
    val isYou = balance.userId == currentUser
    val owed = balance.amount >= 0
    val color = if (owed) SplitzyTheme.accents.moneyIn else SplitzyTheme.accents.moneyOut
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NameAvatar(balance.userId, size = 36.dp)
        Column(Modifier.weight(1f)) {
            Text(
                if (isYou) "You" else balance.userId.substringBefore("@"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                if (kotlin.math.abs(balance.amount) < 0.01) "settled up"
                else if (owed) "gets back" else "owes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            kotlin.math.abs(balance.amount).money(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (kotlin.math.abs(balance.amount) < 0.01) MaterialTheme.colorScheme.onSurfaceVariant else color
        )
    }
}

@Composable
private fun EmptyTabState(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NameAvatar(name: String, size: Dp = 40.dp) {
    Box(
        modifier = Modifier.size(size).background(avatarColorFor(name), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            name.trim().take(1).uppercase().ifEmpty { "?" },
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun payerLabel(payer: String, currentUser: String?): String =
    if (payer == currentUser) "You paid" else "${payer.substringBefore("@")} paid"

private fun Long.asShortDate(): String =
    SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(this))

private fun Double.money(): String = String.format(Locale.US, "₹%,.0f", this)
