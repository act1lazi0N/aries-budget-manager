package com.example.project_budget.ui.screen.settings

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_budget.data.CsvExporter
import com.example.project_budget.data.JsonExporter
import com.example.project_budget.data.TransactionTemp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.Wallet
import com.example.project_budget.ui.components.StatCard
import com.example.project_budget.ui.theme.Project_BudgetTheme

private const val DefaultStatus =
    "Sẵn sàng nhập/xuất dữ liệu giao dịch bằng CSV hoặc JSON."

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    transactions: List<Transaction>,
    wallets: List<Wallet>,
    onImportCsvClick: () -> Unit,
    onImportJsonClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var statusMessage by remember { mutableStateOf(DefaultStatus) }

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri == null) {
            statusMessage = "Đã hủy xuất CSV."
        } else {
            val content = CsvExporter.exportToCsv(transactions.toExportRows(wallets))
            statusMessage = writeExportFile(
                context = context,
                uri = uri,
                content = content,
                successMessage = "Đã xuất CSV thành công."
            )
        }
    }

    val jsonExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) {
            statusMessage = "Đã hủy xuất JSON."
        } else {
            val content = JsonExporter.exportToJson(transactions.toExportRows(wallets))
            statusMessage = writeExportFile(
                context = context,
                uri = uri,
                content = content,
                successMessage = "Đã xuất JSON thành công."
            )
        }
    }

    val csvImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        statusMessage = if (uri == null) {
            "Đã hủy nhập CSV."
        } else {
            readImportFile(context, uri).fold(
                onSuccess = { content ->
                    val importedCount = CsvExporter.importFromCsv(content).size
                    "Đã đọc CSV: $importedCount giao dịch hợp lệ."
                },
                onFailure = { "Không thể đọc file CSV. Vui lòng thử lại." }
            )
        }
    }

    val jsonImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        statusMessage = if (uri == null) {
            "Đã hủy nhập JSON."
        } else {
            readImportFile(context, uri).fold(
                onSuccess = { content ->
                    val importedCount = JsonExporter.importFromJson(content).size
                    "Đã đọc JSON: $importedCount giao dịch hợp lệ."
                },
                onFailure = { "Không thể đọc file JSON. Vui lòng thử lại." }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Cài đặt") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle(text = "Dữ liệu")
            StatCard(
                title = "Nhập / xuất",
                value = "Hỗ trợ CSV và JSON cho dữ liệu giao dịch"
            )
            StatCard(
                title = "Trạng thái",
                value = statusMessage
            )
            Button(
                onClick = {
                    statusMessage = "Đã chọn xuất CSV. Đang chuẩn bị dữ liệu giao dịch."
                    csvExportLauncher.launch("aries_transactions.csv")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Xuất CSV")
            }
            Button(
                onClick = {
                    statusMessage = "Đã chọn xuất JSON. Đang chuẩn bị dữ liệu giao dịch."
                    jsonExportLauncher.launch("aries_transactions.json")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Xuất JSON")
            }
            OutlinedButton(
                onClick = {
                    onImportCsvClick()
                    // TODO: Persist imported transactions after duplicate/overwrite rules are finalized.
                    statusMessage = "Đã chọn nhập CSV. Vui lòng chọn file CSV hợp lệ."
                    csvImportLauncher.launch(arrayOf("text/*", "text/csv", "application/vnd.ms-excel"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Nhập CSV")
            }
            OutlinedButton(
                onClick = {
                    onImportJsonClick()
                    // TODO: Persist imported transactions after duplicate/overwrite rules are finalized.
                    statusMessage = "Đã chọn nhập JSON. Vui lòng chọn file JSON hợp lệ."
                    jsonImportLauncher.launch(arrayOf("application/json", "text/*"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Nhập JSON")
            }

            SectionTitle(text = "Ứng dụng")
            OutlinedButton(
                onClick = onAboutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Giới thiệu Aries")
            }
        }
    }
}

private fun List<Transaction>.toExportRows(wallets: List<Wallet>): List<TransactionTemp> {
    return map { transaction ->
        TransactionTemp(
            amount = transaction.amount,
            type = transaction.type.displayName,
            category = transaction.category,
            wallet = wallets.firstOrNull { it.id == transaction.walletId }?.name.orEmpty(),
            date = transaction.date,
            note = transaction.note
        )
    }
}

private fun readImportFile(context: Context, uri: Uri): Result<String> {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
            reader.readText()
        } ?: error("Cannot open input stream.")
    }
}

private fun writeExportFile(
    context: Context,
    uri: Uri,
    content: String,
    successMessage: String
): String {
    return runCatching {
        context.contentResolver.openOutputStream(uri)?.use { output ->
            output.write(content.toByteArray(Charsets.UTF_8))
        } ?: error("Cannot open output stream.")
    }.fold(
        onSuccess = { successMessage },
        onFailure = { "Không thể ghi file. Vui lòng thử lại." }
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    Project_BudgetTheme {
        SettingsScreen(
            transactions = emptyList(),
            wallets = emptyList(),
            onImportCsvClick = {},
            onImportJsonClick = {},
            onAboutClick = {}
        )
    }
}
