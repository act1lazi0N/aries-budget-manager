package com.example.project_budget.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.ui.components.BalanceCard
import com.example.project_budget.ui.components.BudgetProgressCard
import com.example.project_budget.ui.components.EmptyState
import com.example.project_budget.ui.components.StatCard
import com.example.project_budget.ui.components.TransactionItem
import com.example.project_budget.ui.components.formatMoney
import com.example.project_budget.ui.theme.Project_BudgetTheme
import com.example.project_budget.viewmodel.BudgetUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: BudgetUiState,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Int) -> Unit,
    onViewAllTransactionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recentTransactions = uiState.transactions.take(3)
    val topCategory = uiState.categoryStats.maxByOrNull { it.value }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Aries")
                        Text(
                            text = "Quản lý ngân sách",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransactionClick) {
                Text(text = "+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BalanceCard(
                balance = uiState.balance,
                totalIncome = uiState.totalIncome,
                totalExpense = uiState.totalExpense
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Chi tháng này",
                    value = formatMoney(uiState.totalExpense),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Giao dịch",
                    value = uiState.totalTransactions.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            if (topCategory != null) {
                StatCard(
                    title = "Danh mục chi nhiều nhất",
                    value = "${topCategory.key} - ${formatMoney(topCategory.value)}"
                )
            }

            if (uiState.budgets.isNotEmpty()) {
                SectionTitle(text = "Ngân sách")
                uiState.budgets.take(2).forEach { budget ->
                    BudgetProgressCard(
                        category = budget.category,
                        spent = uiState.expenseByCategory[budget.category] ?: 0.0,
                        limit = budget.limitAmount
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectionTitle(text = "Giao dịch gần đây")
                TextButton(onClick = onViewAllTransactionsClick) {
                    Text(text = "Xem tất cả")
                }
            }

            if (recentTransactions.isEmpty()) {
                EmptyState(
                    title = "Chưa có giao dịch",
                    message = "Thêm giao dịch đầu tiên để theo dõi ngân sách."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onAddTransactionClick) {
                    Text(text = "Thêm giao dịch")
                }
            } else {
                recentTransactions.forEach { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.id) },
                        onDeleteClick = { },
                        showDeleteAction = false
                    )
                }
            }
        }
    }
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
private fun HomeScreenPreview() {
    Project_BudgetTheme {
        HomeScreen(
            uiState = previewUiState,
            onAddTransactionClick = {},
            onTransactionClick = {},
            onViewAllTransactionsClick = {}
        )
    }
}

private val previewUiState = BudgetUiState(
    transactions = listOf(
        Transaction(
            id = 1,
            title = "Lương tháng",
            amount = 8_000_000.0,
            category = "Lương",
            type = TransactionType.INCOME,
            date = "2026-05-01"
        ),
        Transaction(
            id = 2,
            title = "Ăn trưa",
            amount = 55_000.0,
            category = "Ăn uống",
            type = TransactionType.EXPENSE,
            date = "2026-05-02"
        )
    ),
    totalTransactions = 2,
    totalIncome = 8_000_000.0,
    totalExpense = 55_000.0,
    balance = 7_945_000.0,
    categoryStats = mapOf("Ăn uống" to 55_000.0),
    expenseByCategory = mapOf("Ăn uống" to 55_000.0)
)
