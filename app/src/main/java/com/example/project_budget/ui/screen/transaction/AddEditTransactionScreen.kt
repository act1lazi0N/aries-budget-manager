package com.example.project_budget.ui.screen.transaction


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Hệ màu ấm Pastel mô phỏng chính xác theo thiết kế image_9564ff.png
private val WarmBackground = Color(0xFFF9F5F0) // Màu nền kem cát nhạt
private val WarmSurface = Color(0xFFFFFFFF)    // Màu nền các thẻ/ô nhập trắng tinh
private val PrimaryOrange = Color(0xFFD35400)  // Màu cam đất điểm nhấn nút bấm
private val SecondaryPeach = Color(0xFFFCD6B1) // Màu cam đào nhạt làm nền phụ
private val TextDark = Color(0xFF2C3E50)       // Màu chữ xám đen trầm

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

    // Biến quản lý lỗi validation hiển thị dưới các ô input tương ứng
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

    // Bảng thiết lập tông màu ấm riêng biệt cho màn hình này
    val customColorScheme = MaterialTheme.colorScheme.copy(
        background = WarmBackground,
        surface = WarmSurface,
        primary = PrimaryOrange,
        onPrimary = Color.White,
        secondary = SecondaryPeach,
        onBackground = TextDark
    )

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
                }) { Text("OK", color = PrimaryOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy", color = TextDark) }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PrimaryOrange,
                    todayContentColor = PrimaryOrange
                )
            )
        }
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = WarmBackground
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Ô nhập số tiền (Chặn rỗng, chặn <= 0)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; amountError = null },
                    label = { Text("Số tiền (*)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError != null,
                    supportingText = { amountError?.let { Text(it) } },
                    shape = RoundedCornerShape(16.dp), // Bo tròn góc mượt mà giống như các khối mockup
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        focusedLabelColor = PrimaryOrange
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Tab hoặc nút chọn Loại giao dịch (Thu / Chi)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ElevatedButton(
                        onClick = { type = "Chi" },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (type == "Chi") SecondaryPeach else WarmSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Chi tiêu", color = if (type == "Chi") PrimaryOrange else TextDark)
                    }

                    ElevatedButton(
                        onClick = { type = "Thu" },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (type == "Thu") SecondaryPeach else WarmSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Thu nhập", color = if (type == "Thu") PrimaryOrange else TextDark)
                    }
                }

                // 3. Ô chọn Danh mục (Validate bắt buộc chọn)
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
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            focusedLabelColor = PrimaryOrange
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoriesList.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection, color = TextDark) },
                                onClick = { category = selection; categoryError = null; categoryExpanded = false }
                            )
                        }
                    }
                }

                // 4. Ô chọn Ví tiền trung chuyển
                ExposedDropdownMenuBox(
                    expanded = walletExpanded,
                    onExpandedChange = { walletExpanded = !walletExpanded }
                ) {
                    OutlinedTextField(
                        value = wallet,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ví thanh toán (*)") },
                        isError = walletError != null,
                        supportingText = { walletError?.let { Text(it) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            focusedLabelColor = PrimaryOrange
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = walletExpanded,
                        onDismissRequest = { walletExpanded = false }
                    ) {
                        walletsList.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection, color = TextDark) },
                                onClick = { wallet = selection; walletError = null; walletExpanded = false }
                            )
                        }
                    }
                }

                // 5. Ô chọn Ngày tháng
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ngày giao dịch") },
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryOrange) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // 6. Ô nhập Ghi chú (Validate cảnh báo độ dài vượt 200 kí tự)
                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                        noteError = if (it.length > 200) "Ghi chú không được vượt quá 200 ký tự" else null
                    },
                    label = { Text("Ghi chú") },
                    isError = noteError != null,
                    supportingText = { noteError?.let { Text(it) } },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        focusedLabelColor = PrimaryOrange
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nút kích hoạt lệnh lưu thông tin kèm kiểm tra dữ liệu nghiêm ngặt
                Button(
                    onClick = {
                        val parsedAmount = amount.toDoubleOrNull()
                        var isValid = true

                        if (parsedAmount == null || parsedAmount <= 0) {
                            amountError = "Số tiền phải lớn hơn 0 và không được để trống"
                            isValid = false
                        }
                        if (category.isBlank()) {
                            categoryError = "Vui lòng chọn danh mục phù hợp"
                            isValid = false
                        }
                        if (wallet.isBlank()) {
                            walletError = "Vui lòng lựa chọn ví tài chính"
                            isValid = false
                        }
                        if (note.length > 200) {
                            noteError = "Ghi chú quá dài, vui lòng rút gọn lại"
                            isValid = false
                        }

                        if (isValid) {
                            val isSuccess = onSaveClick(parsedAmount!!, type, category, wallet, date, note)
                            if (isSuccess) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (isEditMode) "Cập nhật dữ liệu thành công!" else "Ghi nhận giao dịch thành công!"
                                    )
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = if (isEditMode) "CẬP NHẬT GIAO DỊCH" else "LƯU GIAO DỊCH",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}