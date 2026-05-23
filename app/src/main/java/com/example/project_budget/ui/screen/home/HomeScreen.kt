package com.example.project_budget.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.ui.navigation.Screen
import com.example.project_budget.utils.formatCurrency

@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    onAddTransactionClick: () -> Unit,
    onViewAllTransactionsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit
) {
    val income = transactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }

    val expense = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

    val balance = income - expense

    val topExpenseCategory = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { it.value.sumOf { t -> t.amount } }
        .maxByOrNull { it.value }
        ?.key ?: "Chưa có dữ liệu"

    val foodExpense = transactions
        .filter { it.type == TransactionType.EXPENSE && it.category == "Ăn uống" }
        .sumOf { it.amount }
    val foodBudget = 1500000.0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch")
            }
        },

        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { onBottomNavClick(Screen.Transactions.route) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Giao dịch") },
                    label = { Text("Giao dịch") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { onBottomNavClick(Screen.Statistics.route) },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Thống kê") },
                    label = { Text("Thống kê") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { onBottomNavClick(Screen.Settings.route) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Cài đặt") },
                    label = { Text("Cài đặt") }
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Aries",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text("Quản lý ngân sách")

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Số dư hiện tại",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${formatCurrency(balance)} đ",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Thu nhập",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "+${formatCurrency(income)} đ",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            Column {
                                Text(
                                    text = "Chi tiêu",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "-${formatCurrency(expense)} đ",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Chi tháng này",
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "${formatCurrency(expense)} đ",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Giao dịch",
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "${transactions.size}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Danh mục chi nhiều nhất",
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = topExpenseCategory,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Ngân sách",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ăn uống",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${formatCurrency(foodExpense)} / ${formatCurrency(foodBudget)} đ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { (foodExpense / foodBudget).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = androidx.compose.ui.graphics.Color(0xFFF57C00),
                            trackColor = androidx.compose.ui.graphics.Color(0xFFF57C00).copy(alpha = 0.2f),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tiến độ ngân sách hàng tháng",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Giao dịch gần đây",
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onViewAllTransactionsClick) {
                        Text(
                            text = "Xem tất cả",
                            color = androidx.compose.ui.graphics.Color(0xFFF57C00),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(transactions.take(3)) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold
                            )
                            Text("${item.category} • ${item.date}")
                        }

                        Text(
                            text = if (item.type == TransactionType.INCOME)
                                "+${formatCurrency(item.amount)} đ"
                            else
                                "-${formatCurrency(item.amount)} đ",
                            color = if (item.type == TransactionType.INCOME)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}