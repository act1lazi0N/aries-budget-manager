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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Category
import com.example.project_budget.model.DEFAULT_CURRENCY
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
    defaultCurrency: String = DEFAULT_CURRENCY,
    supportedCurrencies: List<String> = listOf(DEFAULT_CURRENCY, "USD", "EUR", "JPY", "KRW"),
    isSaving: Boolean = false,
    errorMessage: String? = null,
    onDismissError: () -> Unit = {},
    transaction: Transaction? = null,
    onSaveClick: (Transaction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditMode = transaction != null
    val defaultWalletId = wallets.firstOrNull()?.id ?: 1
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var title by rememberSaveable(transaction?.id) { mutableStateOf(transaction?.title.orEmpty()) }
    var amount by rememberSaveable(transaction?.id) {
        mutableStateOf(transaction?.amount?.takeIf { it > 0.0 }?.toPlainText().orEmpty())
    }
    var currency by rememberSaveable(transaction?.id) { mutableStateOf(transaction?.currency ?: defaultCurrency) }
    var typeName by rememberSaveable(transaction?.id) {
        mutableStateOf((transaction?.type ?: TransactionType.EXPENSE).name)
    }
    val type = runCatching { TransactionType.valueOf(typeName) }.getOrDefault(TransactionType.EXPENSE)
    var category by rememberSaveable(transaction?.id) { mutableStateOf(transaction?.category.orEmpty()) }
    var walletId by rememberSaveable(transaction?.id) { mutableStateOf(transaction?.walletId ?: defaultWalletId) }
    var date by rememberSaveable(transaction?.id) {
        mutableStateOf(transaction?.date?.takeIf { it.isNotBlank() } ?: dateFormatter.format(Date()))
    }
    var note by rememberSaveable(transaction?.id) { mutableStateOf(transaction?.note.orEmpty()) }

    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var amountError by rememberSaveable { mutableStateOf<String?>(null) }
    var currencyError by rememberSaveable { mutableStateOf<String?>(null) }
    var categoryError by rememberSaveable { mutableStateOf<String?>(null) }
    var walletError by rememberSaveable { mutableStateOf<String?>(null) }
    var noteError by rememberSaveable { mutableStateOf<String?>(null) }

    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var currencyExpanded by rememberSaveable { mutableStateOf(false) }
    var walletExpanded by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val categorySuggestions = categories
        .filter { it.type == type }
        .ifEmpty { categories }
    val visibleCategorySuggestions = categorySuggestions.filter { item ->
        category.isBlank() || item.name.contains(category, ignoreCase = true)
    }
    val selectedWalletName = wallets.firstOrNull { it.id == walletId }?.name.orEmpty()
    val parsedAmount = amount.toDoubleOrNull()
    val previewAmount = when {
        parsedAmount == null -> null
        currency == defaultCurrency -> parsedAmount
        transaction?.currency == currency &&
            transaction.convertedCurrency == defaultCurrency &&
            transaction.exchangeRate > 0.0 -> parsedAmount * transaction.exchangeRate
        else -> null
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            title = { Text(text = "Không thể lưu giao dịch") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                TextButton(onClick = onDismissError) {
                    Text(text = "OK")
                }
            }
        )
    }

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
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
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

            ExposedDropdownMenuBox(
                expanded = currencyExpanded,
                onExpandedChange = { currencyExpanded = !currencyExpanded }
            ) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Tiền tệ (*)") },
                    isError = currencyError != null,
                    supportingText = {
                        Text(
                            text = currencyError
                                ?: previewAmount?.let {
                                    "${amount.ifBlank { "0" }} $currency ~= ${it.toPlainText()} $defaultCurrency"
                                }
                                ?: "Sẽ quy đổi sang $defaultCurrency khi lưu giao dịch."
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false }
                ) {
                    supportedCurrencies.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                currency = item
                                currencyError = null
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == TransactionType.EXPENSE,
                        onClick = {
                            typeName = TransactionType.EXPENSE.name
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
                            typeName = TransactionType.INCOME.name
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
                    onValueChange = {
                        category = it
                        categoryError = null
                        categoryExpanded = true
                    },
                    label = { Text(text = "Danh mục (*)") },
                    placeholder = { Text(text = "Chọn hoặc nhập danh mục mới") },
                    isError = categoryError != null,
                    supportingText = {
                        Text(
                            text = categoryError
                                ?: "Có thể chọn danh mục có sẵn hoặc nhập danh mục tùy chỉnh."
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    visibleCategorySuggestions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                category = item.name
                                categoryError = null
                                categoryExpanded = false
                            }
                        )
                    }
                    if (
                        category.isNotBlank() &&
                        categorySuggestions.none { it.name.equals(category.trim(), ignoreCase = true) }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Dùng danh mục mới: ${category.trim()}") },
                            onClick = {
                                category = category.trim()
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
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
                        var isValid = true

                        if (title.isBlank()) {
                            titleError = "Vui lòng nhập tên giao dịch"
                            isValid = false
                        }
                        if (parsedAmount == null || parsedAmount <= 0.0) {
                            amountError = "Số tiền phải lớn hơn 0"
                            isValid = false
                        }
                        if (currency.isBlank() || currency !in supportedCurrencies) {
                            currencyError = "Loại tiền này hiện chưa được hỗ trợ"
                            isValid = false
                        }
                        if (category.isBlank()) {
                            categoryError = "Vui lòng chọn hoặc nhập danh mục"
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
                                    currency = currency,
                                    convertedAmount = transaction?.convertedAmount ?: parsedAmount,
                                    convertedCurrency = defaultCurrency,
                                    exchangeRate = transaction?.exchangeRate ?: 1.0,
                                    category = category.trim(),
                                    type = type,
                                    date = date,
                                    note = note.trim(),
                                    walletId = walletId
                                )
                            )
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when {
                            isSaving -> "Đang lưu..."
                            isEditMode -> "Cập nhật"
                            else -> "Thêm"
                        }
                    )
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
