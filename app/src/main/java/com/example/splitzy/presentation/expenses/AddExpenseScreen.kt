package com.example.splitzy.presentation.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.splitzy.domain.model.Category
import com.example.splitzy.ui.theme.avatarColorFor
import com.example.splitzy.ui.theme.categoryStyleFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    groupId: String,
    onBack: () -> Unit,
    onManageCategories: (String) -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    LaunchedEffect(groupId) { viewModel.selectGroup(groupId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser = remember { viewModel.currentUserEmail() }
    val members = uiState.group?.memberIds ?: emptyList()

    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var paidBy by remember { mutableStateOf<String?>(null) }
    var splitWith by remember { mutableStateOf<Set<String>>(emptySet()) }

    var showCategorySheet by remember { mutableStateOf(false) }
    var showPaidBySheet by remember { mutableStateOf(false) }

    // Members arrive a frame or two after the screen does; default to everyone
    // splitting and the signed-in user paying, which is the common case.
    LaunchedEffect(members) {
        if (members.isNotEmpty()) {
            if (splitWith.isEmpty()) splitWith = members.toSet()
            if (paidBy == null) paidBy = currentUser?.takeIf { it in members } ?: members.first()
        }
    }

    val selectedCategory = uiState.categories.find { it.id == selectedCategoryId }
    val amount = amountText.toDoubleOrNull()
    val canSave = amount != null && amount > 0 &&
        description.isNotBlank() && !paidBy.isNullOrBlank() && splitWith.isNotEmpty()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // The amount is the point of this screen, so it gets the weight.
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "₹",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        placeholder = {
                            Text("0", style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold))
                        },
                        textStyle = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            FieldLabel("Description")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Dinner, Petrol, Tickets...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("Category")
            SelectorRow(
                text = selectedCategory?.name ?: "Select a category",
                muted = selectedCategory == null,
                leading = selectedCategory?.let { category ->
                    {
                        val style = categoryStyleFor(category.name)
                        Box(
                            modifier = Modifier.size(28.dp).background(style.color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(style.icon, null, tint = Color.White, modifier = Modifier.size(15.dp))
                        }
                    }
                },
                onClick = { showCategorySheet = true }
            )

            FieldLabel("Paid by")
            SelectorRow(
                text = paidBy?.let { if (it == currentUser) "You ($it)" else it } ?: "Select who paid",
                muted = paidBy == null,
                leading = paidBy?.let { payer ->
                    { Avatar(payer, 28.dp) }
                },
                onClick = { showPaidBySheet = true }
            )

            FieldLabel("Split with")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    members.chunked(2).forEach { rowMembers ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowMembers.forEach { member ->
                                val checked = member in splitWith
                                FilterChip(
                                    selected = checked,
                                    onClick = {
                                        splitWith =
                                            if (checked) splitWith - member else splitWith + member
                                    },
                                    label = {
                                        Text(
                                            if (member == currentUser) "You" else member.substringBefore("@"),
                                            maxLines = 1
                                        )
                                    },
                                    leadingIcon = {
                                        if (checked) {
                                            Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                                        } else {
                                            Avatar(member, 18.dp)
                                        }
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.addExpense(
                        description = description.trim(),
                        amount = amount ?: return@Button,
                        paidByUserId = paidBy.orEmpty(),
                        splitBetween = splitWith.toList(),
                        categoryId = selectedCategoryId
                    )
                    onBack()
                },
                enabled = canSave,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp)
            ) {
                Text("Add Expense", modifier = Modifier.padding(vertical = 6.dp), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showCategorySheet) {
        CategorySheet(
            categories = uiState.categories,
            selectedCategoryId = selectedCategoryId,
            onSelect = { selectedCategoryId = it; showCategorySheet = false },
            onAddCategory = viewModel::addCategory,
            onManageCategories = {
                showCategorySheet = false
                onManageCategories(groupId)
            },
            onDismiss = { showCategorySheet = false }
        )
    }

    if (showPaidBySheet) {
        PaidBySheet(
            members = members,
            currentUser = currentUser,
            selected = paidBy,
            onSelect = { paidBy = it; showPaidBySheet = false },
            onDismiss = { showPaidBySheet = false }
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun SelectorRow(
    text: String,
    muted: Boolean,
    leading: (@Composable () -> Unit)?,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            leading?.invoke()
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (muted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun Avatar(name: String, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier.size(size).background(avatarColorFor(name), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            name.trim().take(1).uppercase().ifEmpty { "?" },
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = if (size.value < 24) 10.sp else 14.sp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySheet(
    categories: List<Category>,
    selectedCategoryId: String?,
    onSelect: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onManageCategories: () -> Unit,
    onDismiss: () -> Unit
) {
    var newCategoryText by remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp)) {
            Text(
                "Choose Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            categories.forEach { category ->
                val style = categoryStyleFor(category.name)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(category.id) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).background(style.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(style.icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text(category.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    if (category.id == selectedCategoryId) {
                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                OutlinedTextField(
                    value = newCategoryText,
                    onValueChange = { newCategoryText = it },
                    placeholder = { Text("Add new category") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    enabled = newCategoryText.isNotBlank(),
                    onClick = { onAddCategory(newCategoryText.trim()); newCategoryText = "" }
                ) { Text("Add") }
            }
            TextButton(onClick = onManageCategories, modifier = Modifier.padding(top = 4.dp)) {
                Text("Manage categories")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaidBySheet(
    members: List<String>,
    currentUser: String?,
    selected: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp)) {
            Text(
                "Who paid?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            members.forEach { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(member) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Avatar(member, 36.dp)
                    Text(
                        if (member == currentUser) "You ($member)" else member,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    if (member == selected) {
                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
