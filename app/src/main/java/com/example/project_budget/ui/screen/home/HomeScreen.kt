package com.example.project_budget.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.model.Wallet
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
    val walletNamesById = uiState.wallets.associate { wallet -> wallet.id to wallet.name }

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
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Thêm giao dịch"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BalanceCard(
                    balance = uiState.balance,
                    totalIncome = uiState.totalIncome,
                    totalExpense = uiState.totalExpense
                )
            }

            item {
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
            }

            if (topCategory != null) {
                item {
                    StatCard(
                        title = "Danh mục chi nhiều nhất",
                        value = "${topCategory.key} - ${formatMoney(topCategory.value)}"
                    )
                }
            }

            if (uiState.budgets.isNotEmpty()) {
                item {
                    SectionTitle(text = "Ngân sách")
                }
                items(uiState.budgets.take(2)) { budget ->
                    BudgetProgressCard(
                        category = budget.category,
                        spent = uiState.expenseByCategory[budget.category] ?: 0.0,
                        limit = budget.limitAmount
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Giao dịch gần đây")
                    TextButton(onClick = onViewAllTransactionsClick) {
                        Text(text = "Xem tất cả")
                    }
                }
            }

            if (recentTransactions.isEmpty()) {
                item {
                    EmptyState(
                        title = "Chưa có giao dịch",
                        message = "Thêm giao dịch đầu tiên để theo dõi ngân sách.",
                        actionText = "Thêm giao dịch",
                        onActionClick = onAddTransactionClick
                    )
                }
            } else {
                items(recentTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.id) },
                        onDeleteClick = { },
                        showDeleteAction = false,
                        walletName = walletNamesById[transaction.walletId]
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
    wallets = listOf(
        Wallet(id = 1, name = "Tiền mặt", balance = 7_945_000.0)
    ),
    totalTransactions = 2,
    totalIncome = 8_000_000.0,
    totalExpense = 55_000.0,
    balance = 7_945_000.0,
    categoryStats = mapOf("Ăn uống" to 55_000.0),
    expenseByCategory = mapOf("Ăn uống" to 55_000.0)
)
