package com.example.splitzy.presentation.expenses

import android.content.Intent
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.splitzy.domain.model.Category
import com.example.splitzy.domain.model.Expense
import com.example.splitzy.ui.theme.MoneyIn
import com.example.splitzy.ui.theme.MoneyInContainer
import com.example.splitzy.ui.theme.avatarColorFor
import com.example.splitzy.ui.theme.categoryStyleFor
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    groupId: String,
    groupName: String,
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    LaunchedEffect(groupId) { viewModel.selectGroup(groupId) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val members = uiState.group?.memberIds ?: emptyList()

    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(groupName, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    text = { Text("Add expense") },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(50)
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
                SecondaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Expenses") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Members") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Settle Up") })
                }
                when (selectedTab) {
                    0 -> ExpensesTab(
                        expenses = uiState.expenses,
                        categories = uiState.categories,
                        modifier = Modifier.weight(1f)
                    )
                    1 -> MembersTab(members = members, modifier = Modifier.weight(1f))
                    else -> SettleUpTab(
                        settlements = uiState.settlements,
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

    if (showAddDialog) {
        AddExpenseDialog(
            members = members,
            categories = uiState.categories,
            onAddCategory = viewModel::addCategory,
            onDeleteCategory = viewModel::deleteCategory,
            onConfirm = { description, amount, paidBy, splitBetween, categoryId ->
                viewModel.addExpense(description, amount, paidBy, splitBetween, categoryId)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun ExpensesTab(expenses: List<Expense>, categories: List<Category>, modifier: Modifier = Modifier) {
    if (expenses.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No expenses yet. Tap \"Add expense\" to log one.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(expenses, key = { it.id }) { expense ->
            val categoryName = categories.find { it.id == expense.categoryId }?.name
            ExpenseRow(expense, categoryName)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun MembersTab(members: List<String>, modifier: Modifier = Modifier) {
    if (members.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No members yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        itemsIndexed(members) { index, member ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NameAvatar(member, size = 40.dp)
                Text(member, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                if (index == 0) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(50)) {
                        Text(
                            "Owner",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun SettleUpTab(
    settlements: List<Pair<String, Pair<String, Double>>>,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        if (settlements.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Everyone is settled up", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(settlements) { (debtor, creditorAndAmount) ->
                    val (creditor, amount) = creditorAndAmount
                    SettlementRow(debtor = debtor, creditor = creditor, amount = amount)
                }
            }
        }
        Button(
            onClick = onShare,
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) { Text("Share Settlement Summary") }
    }
}

@Composable
private fun NameAvatar(name: String, size: Dp = 32.dp) {
    val color = avatarColorFor(name)
    Box(
        modifier = Modifier.size(size).background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            name.trim().take(1).uppercase().ifEmpty { "?" },
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettlementRow(debtor: String, creditor: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NameAvatar(debtor)
        Text(debtor, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "owes",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        NameAvatar(creditor)
        Text(
            creditor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f).padding(start = 4.dp)
        )
        Surface(
            color = MoneyInContainer,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                amount.money(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MoneyIn
            )
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense, categoryName: String?) {
    val style = categoryStyleFor(categoryName ?: "")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (categoryName != null) style.color else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                style.icon,
                contentDescription = categoryName,
                tint = if (categoryName != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(expense.description, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(
                "Paid by ${expense.paidByUserId} · split ${expense.splitBetween.size} ways",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(expense.amount.money(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

// A read-only field that looks like a text field but opens a picker dialog —
// Category / Paid by / Split With all work this way instead of free text,
// since they must come from the group's real categories/members.
@Composable
private fun PickerField(label: String, value: String, placeholder: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier.matchParentSize().clickable(onClick = onClick)
        )
    }
}

@Composable
private fun AddExpenseDialog(
    members: List<String>,
    categories: List<Category>,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onConfirm: (description: String, amount: Double, paidBy: String, splitBetween: List<String>, categoryId: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf(members.firstOrNull().orEmpty()) }
    var splitBetween by remember { mutableStateOf(members) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    var showCategoryPicker by remember { mutableStateOf(false) }
    var showPaidByPicker by remember { mutableStateOf(false) }
    var showSplitPicker by remember { mutableStateOf(false) }

    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Dinner") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                PickerField(
                    label = "Category",
                    value = selectedCategoryName.orEmpty(),
                    placeholder = "Select a category",
                    onClick = { showCategoryPicker = true }
                )
                PickerField(
                    label = "Paid by",
                    value = paidBy,
                    placeholder = "Select who paid",
                    onClick = { showPaidByPicker = true }
                )
                PickerField(
                    label = "Split with",
                    value = splitBetween.joinToString(", "),
                    placeholder = "Select members",
                    onClick = { showSplitPicker = true }
                )
            }
        },
        confirmButton = {
            Button(
                enabled = amountText.toDoubleOrNull() != null &&
                    description.isNotBlank() &&
                    paidBy.isNotBlank() &&
                    splitBetween.isNotEmpty(),
                shape = RoundedCornerShape(50),
                onClick = {
                    onConfirm(
                        description.trim(),
                        amountText.toDoubleOrNull() ?: return@Button,
                        paidBy,
                        splitBetween,
                        selectedCategoryId
                    )
                }
            ) { Text("Add Expense") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    if (showCategoryPicker) {
        CategoryPickerDialog(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onSelect = { selectedCategoryId = it },
            onAddCategory = onAddCategory,
            onDeleteCategory = { category ->
                if (category.id == selectedCategoryId) selectedCategoryId = null
                onDeleteCategory(category)
            },
            onDismiss = { showCategoryPicker = false }
        )
    }
    if (showPaidByPicker) {
        MemberPickerDialog(
            title = "Paid by",
            members = members,
            selected = paidBy,
            onSelect = { paidBy = it },
            onDismiss = { showPaidByPicker = false }
        )
    }
    if (showSplitPicker) {
        SplitWithPickerDialog(
            members = members,
            selected = splitBetween,
            onConfirm = { splitBetween = it; showSplitPicker = false },
            onDismiss = { showSplitPicker = false }
        )
    }
}

// Doubles as "manage categories": every category shown here can be deleted,
// and a new one can be added inline — there's no separate management screen.
@Composable
private fun CategoryPickerDialog(
    categories: List<Category>,
    selectedCategoryId: String?,
    onSelect: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    var newCategoryText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Category") },
        text = {
            Column {
                categories.forEach { category ->
                    val style = categoryStyleFor(category.name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(category.id); onDismiss() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp).background(style.color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(style.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text(category.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        if (category.id == selectedCategoryId) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onDeleteCategory(category) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Delete ${category.name}", modifier = Modifier.size(16.dp))
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCategoryText,
                        onValueChange = { newCategoryText = it },
                        placeholder = { Text("New category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        enabled = newCategoryText.isNotBlank(),
                        onClick = { onAddCategory(newCategoryText.trim()); newCategoryText = "" }
                    ) { Text("Add") }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } }
    )
}

@Composable
private fun MemberPickerDialog(
    title: String,
    members: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                members.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(member); onDismiss() }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NameAvatar(member, size = 28.dp)
                        Text(member, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        if (member == selected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun SplitWithPickerDialog(
    members: List<String>,
    selected: List<String>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var picked by remember { mutableStateOf(selected.toSet()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Split With") },
        text = {
            Column {
                members.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                picked = if (member in picked) picked - member else picked + member
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = member in picked,
                            onCheckedChange = { checked ->
                                picked = if (checked) picked + member else picked - member
                            }
                        )
                        NameAvatar(member, size = 28.dp)
                        Text(member, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            Button(shape = RoundedCornerShape(50), onClick = { onConfirm(picked.toList()) }) { Text("Done") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun Double.money(): String = String.format(Locale.US, "₹%.2f", this)
