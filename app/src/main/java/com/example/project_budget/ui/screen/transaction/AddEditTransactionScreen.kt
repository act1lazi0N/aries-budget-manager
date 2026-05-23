package com.example.project_budget.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    initialAmount: String = "",
    initialType: String = "Chi",
    initialCategory: String = "",
    initialWallet: String = "",
    initialDate: String = "",
    initialNote: String = "",
    isEditMode: Boolean = false,
    onSaveClick: (amount: Double, type: String, category: String, wallet: String, date: String, note: String) -> Boolean
) {
    var amount by remember { mutableStateOf(initialAmount) }
    var type by remember { mutableStateOf(initialType) }
    var category by remember { mutableStateOf(initialCategory) }
    var wallet by remember { mutableStateOf(initialWallet) }
    var date by remember { mutableStateOf(if (initialDate.isEmpty()) SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) else initialDate) }
    var note by remember { mutableStateOf(initialNote) }

    // State quản lý lỗi hiển thị bằng TextField error
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var walletError by remember { mutableStateOf<String?>(null) }
    var noteError by remember { mutableStateOf<String?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var walletExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val categoriesList = listOf("Ăn uống", "Di chuyển", "Mua sắm", "Lương", "Thưởng")
    val walletsList = listOf("Tiền mặt", "Tài khoản ngân hàng", "Ví điện tử")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        date = formatter.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Ô nhập số tiền (Validate không rỗng, > 0)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; amountError = null },
                label = { Text("Số tiền (*)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = amountError != null,
                supportingText = { amountError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Loại giao dịch (Thu/Chi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "Chi", onClick = { type = "Chi" })
                    Text("Chi tiêu")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "Thu", onClick = { type = "Thu" })
                    Text("Thu nhập")
                }
            }

            // 3. Chọn danh mục (Validate bắt buộc chọn)
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Danh mục (*)") },
                    isError = categoryError != null,
                    supportingText = { categoryError?.let { Text(it) } },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categoriesList.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection) },
                            onClick = { category = selection; categoryError = null; categoryExpanded = false }
                        )
                    }
                }
            }

            // 4. Chọn Ví (Form yêu cầu)
            ExposedDropdownMenuBox(
                expanded = walletExpanded,
                onExpandedChange = { walletExpanded = !walletExpanded }
            ) {
                OutlinedTextField(
                    value = wallet,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ví (*)") },
                    isError = walletError != null,
                    supportingText = { walletError?.let { Text(it) } },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = walletExpanded, onDismissRequest = { walletExpanded = false }) {
                    walletsList.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection) },
                            onClick = { wallet = selection; walletError = null; walletExpanded = false }
                        )
                    }
                }
            }

            // 5. Chọn ngày giao dịch
            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text("Ngày giao dịch") },
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // 6. Ghi chú (Validate không quá dài - chặn tối đa 200 ký tự)
            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                    noteError = if (it.length > 200) "Ghi chú không được vượt quá 200 ký tự" else null
                },
                label = { Text("Ghi chú") },
                isError = noteError != null,
                supportingText = { noteError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // Nút Lưu kết hợp Validation toàn diện & thông báo Snackbar
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    var isValid = true

                    if (parsedAmount == null || parsedAmount <= 0) {
                        amountError = "Số tiền phải lớn hơn 0 và không để rỗng"
                        isValid = false
                    }
                    if (category.isBlank()) {
                        categoryError = "Vui lòng chọn danh mục"
                        isValid = false
                    }
                    if (wallet.isBlank()) {
                        walletError = "Vui lòng chọn ví"
                        isValid = false
                    }
                    if (note.length > 200) {
                        noteError = "Ghi chú quá dài"
                        isValid = false
                    }

                    if (isValid) {
                        val isSuccess = onSaveClick(parsedAmount!!, type, category, wallet, date, note)
                        if (isSuccess) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (isEditMode) "Cập nhật giao dịch thành công!" else "Thêm giao dịch thành công!"
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Cập Nhật Giao Dịch" else "Thêm Giao Dịch")
            }
        }
    }
}

