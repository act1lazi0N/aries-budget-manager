package com.example.project_budget.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_budget.data.TransactionRepository
import com.example.project_budget.model.TransactionType

@Composable
fun TransactionListScreen(
    navController: NavController,
    repository: TransactionRepository
) {
    var transactions by remember {
        mutableStateOf(repository.getAllTransactions())
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_transaction")
                }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Button(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Quay lại")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Danh sách giao dịch",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(transactions) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clickable {
                                navController.navigate("edit_transaction/${item.id}")
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Danh mục: ${item.category}")

                            Text(
                                text = if (item.type == TransactionType.INCOME)
                                    "Loại: Thu nhập"
                                else
                                    "Loại: Chi tiêu"
                            )

                            Text("Số tiền: ${item.amount} VNĐ")

                            Text("Ngày: ${item.date}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    repository.deleteTransaction(item.id)
                                    transactions = repository.getAllTransactions()
                                }
                            ) {
                                Text("Xóa")
                            }
                        }
                    }
                }
            }
        }
    }
}