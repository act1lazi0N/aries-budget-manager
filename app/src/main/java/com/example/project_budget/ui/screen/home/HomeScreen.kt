package com.example.project_budget.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.ui.components.*
import com.example.project_budget.ui.navigation.Screen
import com.example.project_budget.utils.formatCurrency

@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    onAddTransactionClick: () -> Unit,           // FAB chuyển sang màn thêm
    onEditTransactionClick: (Transaction) -> Unit, // Bấm vào item chuyển sang màn sửa
    onViewAllTransactionsClick: () -> Unit,      // Chuyển sang màn danh sách giao dịch
    onBottomNavClick: (String) -> Unit           // Điều hướng Bottom Navigation
) {
    // 1. Tính toán số liệu tổng quan
    val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val balance = income - expense

    val topExpenseEntry = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .maxByOrNull { it.value.sumOf { t -> t.amount } }

    val topExpenseCategory = topExpenseEntry?.key ?: "Chưa có dữ liệu"
    val topExpenseAmount = topExpenseEntry?.value?.sumOf { it.amount } ?: 0.0

    // Giả lập dữ liệu ngân sách (Sẽ lấy từ Database ở các bước sau)
    val foodExpense = transactions
        .filter { it.type == TransactionType.EXPENSE && it.category == "Ăn uống" }
        .sumOf { it.amount }
    val foodBudget = 1_500_000.0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // NHIỆM VỤ: Tạo FloatingActionButton để đi tới màn thêm giao dịch
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch")
            }
        },
        // NHIỆM VỤ: Tạo Bottom Navigation
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Đang ở Home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onBottomNavClick(Screen.Transactions.route) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    label = { Text("Giao dịch") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Điều hướng tới Statistics */ },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text("Thống kê") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Điều hướng tới Settings */ },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Cài đặt") }
                )
            }
        }
    ) { padding ->
        // NHIỆM VỤ: Dùng LazyColumn để hiển thị nội dung và danh sách giao dịch
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aries", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Quản lý tài chính cá nhân", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                Spacer(modifier = Modifier.height(20.dp))

                // COMPONENT: BalanceCard (Nền màu hệ thống - PrimaryContainer)
                BalanceCard(balance = balance, income = income, expense = expense)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        // COMPONENT: StatCard (Nền Trắng)
                        StatCard(title = "Chi tháng này", value = "${formatCurrency(expense)} ₫")
                    }
                    Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        // COMPONENT: StatCard (Nền Trắng)
                        StatCard(title = "Giao dịch", value = "${transactions.size}")
                    }
                }
            }

            item {
                // COMPONENT: StatCard (Nền Trắng) - Hiển thị danh mục chi nhiều nhất
                StatCard(
                    title = "Danh mục chi nhiều nhất",
                    value = if (topExpenseEntry != null) "$topExpenseCategory - ${formatCurrency(topExpenseAmount)} đ" else "Chưa có dữ liệu",
                    valueColor = MaterialTheme.colorScheme.tertiary
                )
            }

            item {
                Text("Ngân sách", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ăn uống", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("${formatCurrency(foodExpense)} / ${formatCurrency(foodBudget)} ₫")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (foodExpense / foodBudget).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Giao dịch gần đây",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onViewAllTransactionsClick) {
                        Text("Xem tất cả", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // NHIỆM VỤ: Hiển thị 3 giao dịch gần nhất
            // NHIỆM VỤ: Bấm vào item giao dịch thì đi tới màn sửa (onClick = { onEditTransactionClick(item) })
            items(transactions.take(3)) { item ->
                TransactionItem(
                    transaction = item,
                    onClick = { onEditTransactionClick(item) },
                )
            }
            
        }
    }
}