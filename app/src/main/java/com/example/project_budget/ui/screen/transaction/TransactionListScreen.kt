package com.example.project_budget.ui.screen.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.ui.components.CategoryChip
import com.example.project_budget.ui.components.EmptyState
import com.example.project_budget.ui.components.TransactionItem
import com.example.project_budget.ui.theme.Project_BudgetTheme
import com.example.project_budget.viewmodel.BudgetUiState

private enum class TransactionFilter {
    ALL,
    INCOME,
    EXPENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    uiState: BudgetUiState,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }
    val visibleTransactions = when (selectedFilter) {
        TransactionFilter.ALL -> uiState.transactions
        TransactionFilter.INCOME -> uiState.transactions.filter { it.type == TransactionType.INCOME }
        TransactionFilter.EXPENSE -> uiState.transactions.filter { it.type == TransactionType.EXPENSE }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Giao dịch") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransactionClick) {
                Text(text = "+")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(
                        text = "Tất cả",
                        selected = selectedFilter == TransactionFilter.ALL,
                        onClick = { selectedFilter = TransactionFilter.ALL }
                    )
                    CategoryChip(
                        text = "Thu",
                        selected = selectedFilter == TransactionFilter.INCOME,
                        onClick = { selectedFilter = TransactionFilter.INCOME }
                    )
                    CategoryChip(
                        text = "Chi",
                        selected = selectedFilter == TransactionFilter.EXPENSE,
                        onClick = { selectedFilter = TransactionFilter.EXPENSE }
                    )
                }
            }

            if (visibleTransactions.isEmpty()) {
                item {
                    EmptyState(
                        title = "Không có giao dịch",
                        message = "Thay đổi bộ lọc hoặc thêm giao dịch mới."
                    )
                }
            } else {
                itemsIndexed(
                    items = visibleTransactions,
                    key = { index, transaction -> "transaction-${transaction.id}-$index" }
                ) { _, transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.id) },
                        onDeleteClick = { onDeleteClick(transaction.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionListScreenPreview() {
    Project_BudgetTheme {
        TransactionListScreen(
            uiState = BudgetUiState(
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
                )
            ),
            onAddTransactionClick = {},
            onTransactionClick = {},
            onDeleteClick = {}
        )
    }
}
