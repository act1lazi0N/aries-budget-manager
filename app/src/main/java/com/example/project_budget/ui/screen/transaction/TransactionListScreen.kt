package com.example.project_budget.ui.screen.transaction

import android.R.attr.fontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.ui.navigation.Screen
import com.example.project_budget.utils.formatCurrency

@Composable
fun TransactionListScreen(
    transactions: List<Transaction>,
    onBackClick: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onBottomNavClick: (String) -> Unit
) {
    var filter by remember {
        mutableStateOf("all")
    }

    val filtered = when (filter) {
        "income" -> transactions.filter { it.type == TransactionType.INCOME }
        "expense" -> transactions.filter { it.type == TransactionType.EXPENSE }
        else -> transactions
    }

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
                    selected = false,
                    onClick = { onBottomNavClick(Screen.Home.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
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
                .padding(20.dp)
        ) {
            item {
                Text(
                    text = "Giao dịch",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    FilterChip(
                        selected = filter == "all",
                        onClick = { filter = "all" },
                        label = { Text("Tất cả") }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FilterChip(
                        selected = filter == "income",
                        onClick = { filter = "income" },
                        label = { Text("Thu") }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FilterChip(
                        selected = filter == "expense",
                        onClick = { filter = "expense" },
                        label = { Text("Chi") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            items(filtered) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .clickable {
                            onTransactionClick(item.id)
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = if (item.type == TransactionType.INCOME)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.type == TransactionType.INCOME) 
                                    Icons.Default.ArrowUpward 
                                else 
                                    Icons.Default.ArrowDownward,
                                contentDescription = if (item.type == TransactionType.INCOME) "Thu" else "Chi",
                                tint = if (item.type == TransactionType.INCOME)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "${item.category} • ${item.date}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
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

                            Spacer(modifier = Modifier.height(12.dp))

                            TextButton(
                                onClick = {
                                    onDeleteClick(item.id)
                                }
                            ) {
                                Text(
                                    text = "Xóa",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Không có giao dịch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Thay đổi bộ lọc hoặc thêm giao dịch mới.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}