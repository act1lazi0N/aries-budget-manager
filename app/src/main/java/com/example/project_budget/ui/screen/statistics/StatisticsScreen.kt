package com.example.project_budget.ui.screen.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.ui.components.CategoryStatItem
import com.example.project_budget.ui.components.EmptyState
import com.example.project_budget.ui.components.StatCard
import com.example.project_budget.ui.components.formatMoney
import com.example.project_budget.ui.theme.Project_BudgetTheme
import com.example.project_budget.viewmodel.BudgetUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    uiState: BudgetUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Thống kê") }
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
            SectionTitle(text = "Tổng quan")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Giao dịch",
                    value = uiState.totalTransactions.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Số dư",
                    value = formatMoney(uiState.balance),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Tổng thu",
                    value = formatMoney(uiState.totalIncome),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tổng chi",
                    value = formatMoney(uiState.totalExpense),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Trung bình",
                    value = formatMoney(uiState.averageAmount),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Lớn nhất",
                    value = uiState.maxTransaction?.let { formatTransactionSummary(it) } ?: "-",
                    modifier = Modifier.weight(1f)
                )
            }
            StatCard(
                title = "Nhỏ nhất",
                value = uiState.minTransaction?.let { formatTransactionSummary(it) } ?: "-"
            )

            SectionTitle(text = "Theo danh mục")
            if (uiState.expenseByCategory.isEmpty()) {
                EmptyState(
                    title = "Chưa có dữ liệu chi tiêu",
                    message = "Thêm giao dịch chi để xem thống kê theo danh mục."
                )
            } else {
                uiState.expenseByCategory.forEach { (category, amount) ->
                    CategoryStatItem(
                        category = category,
                        amount = amount,
                        percentage = uiState.categoryPercentages[category] ?: 0.0
                    )
                }
            }

            SectionTitle(text = "Cảnh báo ngân sách")
            if (uiState.overBudgetWarnings.isEmpty()) {
                StatCard(
                    title = "Trạng thái",
                    value = "Chưa có danh mục vượt ngân sách"
                )
            } else {
                uiState.overBudgetWarnings.forEach { warning ->
                    StatCard(
                        title = "Vượt ngân sách",
                        value = warning
                    )
                }
            }
        }
    }
}

private fun formatTransactionSummary(transaction: Transaction): String {
    return "${transaction.title} - ${formatMoney(transaction.amount)}"
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
private fun StatisticsScreenPreview() {
    Project_BudgetTheme {
        StatisticsScreen(
            uiState = BudgetUiState(
                transactions = listOf(
                    Transaction(
                        id = 1,
                        title = "Lương tháng",
                        amount = 8_000_000.0,
                        category = "Lương",
                        type = TransactionType.INCOME
                    ),
                    Transaction(
                        id = 2,
                        title = "Ăn trưa",
                        amount = 70_000.0,
                        category = "Ăn uống",
                        type = TransactionType.EXPENSE
                    )
                ),
                totalTransactions = 2,
                totalIncome = 8_000_000.0,
                totalExpense = 70_000.0,
                balance = 7_930_000.0,
                averageAmount = 4_035_000.0,
                maxTransaction = Transaction(
                    id = 1,
                    title = "Lương tháng",
                    amount = 8_000_000.0,
                    category = "Lương",
                    type = TransactionType.INCOME
                ),
                minTransaction = Transaction(
                    id = 2,
                    title = "Ăn trưa",
                    amount = 70_000.0,
                    category = "Ăn uống",
                    type = TransactionType.EXPENSE
                ),
                expenseByCategory = mapOf("Ăn uống" to 70_000.0),
                categoryPercentages = mapOf("Ăn uống" to 100.0)
            )
        )
    }
}
