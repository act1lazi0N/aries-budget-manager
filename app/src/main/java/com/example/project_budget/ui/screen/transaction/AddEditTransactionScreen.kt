package com.example.project_budget.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Category
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.model.Wallet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    categories: List<Category>,
    wallets: List<Wallet>,
    transaction: Transaction? = null,
    onSaveClick: (Transaction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditMode = transaction != null
    val defaultWalletId = wallets.firstOrNull()?.id ?: 1
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var title by remember(transaction?.id) { mutableStateOf(transaction?.title.orEmpty()) }
    var amount by remember(transaction?.id) {
        mutableStateOf(transaction?.amount?.takeIf { it > 0.0 }?.toPlainText().orEmpty())
    }
    var type by remember(transaction?.id) { mutableStateOf(transaction?.type ?: TransactionType.EXPENSE) }
    var category by remember(transaction?.id) { mutableStateOf(transaction?.category.orEmpty()) }
    var walletId by remember(transaction?.id) { mutableStateOf(transaction?.walletId ?: defaultWalletId) }
    var date by remember(transaction?.id) {
        mutableStateOf(transaction?.date?.takeIf { it.isNotBlank() } ?: dateFormatter.format(Date()))
    }
    var note by remember(transaction?.id) { mutableStateOf(transaction?.note.orEmpty()) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var walletError by remember { mutableStateOf<String?>(null) }
    var noteError by remember { mutableStateOf<String?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var walletExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val filteredCategories = categories
        .filter { it.type == type }
        .ifEmpty { categories }
    val selectedWalletName = wallets.firstOrNull { it.id == walletId }?.name.orEmpty()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = dateFormatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Hủy")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (isEditMode) "Sửa giao dịch" else "Thêm giao dịch")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null
                },
                label = { Text(text = "Tên giao dịch (*)") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(text = it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    amountError = null
                },
                label = { Text(text = "Số tiền (*)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError != null,
                supportingText = { amountError?.let { Text(text = it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == TransactionType.EXPENSE,
                        onClick = {
                            type = TransactionType.EXPENSE
                            category = ""
                            categoryError = null
                        }
                    )
                    Text(text = "Chi tiêu")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == TransactionType.INCOME,
                        onClick = {
                            type = TransactionType.INCOME
                            category = ""
                            categoryError = null
                        }
                    )
                    Text(text = "Thu nhập")
                }
            }

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Danh mục (*)") },
                    isError = categoryError != null,
                    supportingText = { categoryError?.let { Text(text = it) } },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    filteredCategories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                category = item.name
                                categoryError = null
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = walletExpanded,
                onExpandedChange = { walletExpanded = !walletExpanded }
            ) {
                OutlinedTextField(
                    value = selectedWalletName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Ví (*)") },
                    isError = walletError != null,
                    supportingText = { walletError?.let { Text(text = it) } },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = walletExpanded,
                    onDismissRequest = { walletExpanded = false }
                ) {
                    wallets.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                walletId = item.id
                                walletError = null
                                walletExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Ngày giao dịch") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Chọn ngày"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                    noteError = if (it.length > 200) {
                        "Ghi chú không được vượt quá 200 ký tự"
                    } else {
                        null
                    }
                },
                label = { Text(text = "Ghi chú") },
                isError = noteError != null,
                supportingText = { noteError?.let { Text(text = it) } },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Hủy")
                }
                Button(
                    onClick = {
                        val parsedAmount = amount.toDoubleOrNull()
                        var isValid = true

                        if (title.isBlank()) {
                            titleError = "Vui lòng nhập tên giao dịch"
                            isValid = false
                        }
                        if (parsedAmount == null || parsedAmount <= 0.0) {
                            amountError = "Số tiền phải lớn hơn 0"
                            isValid = false
                        }
                        if (category.isBlank()) {
                            categoryError = "Vui lòng chọn danh mục"
                            isValid = false
                        }
                        if (wallets.none { it.id == walletId }) {
                            walletError = "Vui lòng chọn ví"
                            isValid = false
                        }
                        if (note.length > 200) {
                            noteError = "Ghi chú quá dài"
                            isValid = false
                        }

                        if (isValid && parsedAmount != null) {
                            onSaveClick(
                                Transaction(
                                    id = transaction?.id ?: 0,
                                    title = title.trim(),
                                    amount = parsedAmount,
                                    category = category,
                                    type = type,
                                    date = date,
                                    note = note.trim(),
                                    walletId = walletId
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (isEditMode) "Cập nhật" else "Thêm")
                }
            }
        }
    }
}

private fun Double.toPlainText(): String {
    return if (this % 1.0 == 0.0) {
        toLong().toString()
    } else {
        toString()
    }
}
