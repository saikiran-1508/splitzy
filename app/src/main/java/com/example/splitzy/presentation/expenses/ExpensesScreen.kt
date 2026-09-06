package com.example.splitzy.presentation.expenses

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    val context = LocalContext.current

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
                },
                actions = {
                    IconButton(onClick = {
                        val report = viewModel.buildReport(groupName)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "$groupName — Splitzy report")
                            putExtra(Intent.EXTRA_TEXT, report)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share report"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share report")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add expense") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(50)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                if (uiState.settlements.isNotEmpty()) {
                    item { SectionHeader("Who pays whom") }
                    items(uiState.settlements) { (debtor, creditorAndAmount) ->
                        val (creditor, amount) = creditorAndAmount
                        SettlementRow(debtor = debtor, creditor = creditor, amount = amount)
                    }
                }

                item { SectionHeader("Expenses") }
                if (uiState.expenses.isEmpty()) {
                    item {
                        Text(
                            "No expenses yet. Tap \"Add expense\" to log one.",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(uiState.expenses, key = { it.id }) { expense ->
                        val categoryName = uiState.categories.find { it.id == expense.categoryId }?.name
                        ExpenseRow(expense, categoryName)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
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
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun NameAvatar(name: String, size: androidx.compose.ui.unit.Dp = 32.dp) {
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
            .padding(horizontal = 16.dp, vertical = 6.dp),
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

@Composable
private fun AddExpenseDialog(
    categories: List<Category>,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onConfirm: (description: String, amount: Double, paidBy: String, splitBetween: List<String>, categoryId: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf("") }
    var splitText by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var newCategoryText by remember { mutableStateOf("") }

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
                OutlinedTextField(
                    value = paidBy,
                    onValueChange = { paidBy = it },
                    label = { Text("Paid by") },
                    placeholder = { Text("Alex") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = splitText,
                    onValueChange = { splitText = it },
                    label = { Text("Split between") },
                    placeholder = { Text("Alex, Sam, Priya") },
                    supportingText = { Text("Comma separated") },
                    singleLine = true
                )

                Text("Category (optional)", style = MaterialTheme.typography.labelLarge)
                if (categories.isNotEmpty()) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val style = categoryStyleFor(category.name)
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = {
                                    selectedCategoryId =
                                        if (selectedCategoryId == category.id) null else category.id
                                },
                                label = { Text(category.name) },
                                leadingIcon = {
                                    Icon(
                                        style.icon,
                                        contentDescription = null,
                                        tint = style.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Delete ${category.name}",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable {
                                                if (selectedCategoryId == category.id) selectedCategoryId = null
                                                onDeleteCategory(category)
                                            }
                                    )
                                }
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCategoryText,
                        onValueChange = { newCategoryText = it },
                        label = { Text("New category") },
                        placeholder = { Text("Vegetables") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        enabled = newCategoryText.isNotBlank(),
                        onClick = {
                            onAddCategory(newCategoryText.trim())
                            newCategoryText = ""
                        }
                    ) { Text("Add") }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = amountText.toDoubleOrNull() != null && description.isNotBlank(),
                shape = RoundedCornerShape(50),
                onClick = {
                    onConfirm(
                        description.trim(),
                        amountText.toDoubleOrNull() ?: return@Button,
                        paidBy.trim(),
                        splitText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        selectedCategoryId
                    )
                }
            ) { Text("Add Expense") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun Double.money(): String = String.format(Locale.US, "₹%.2f", this)
