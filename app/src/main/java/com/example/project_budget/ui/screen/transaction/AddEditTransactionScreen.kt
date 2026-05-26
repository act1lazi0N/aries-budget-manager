package com.example.project_budget.ui.screen.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_budget.data.TransactionRepository
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType

@Composable
fun AddEditTransactionScreen(
    navController: NavController,
    repository: TransactionRepository,
    transactionId: Int?
) {
    val oldTransaction = transactionId?.let {
        repository.getTransactionById(it)
    }

    var title by remember {
        mutableStateOf(oldTransaction?.title ?: "")
    }

    var amount by remember {
        mutableStateOf(oldTransaction?.amount?.toString() ?: "")
    }

    var category by remember {
        mutableStateOf(oldTransaction?.category ?: "Ăn uống")
    }

    var date by remember {
        mutableStateOf(oldTransaction?.date ?: "2026-05-23")
    }

    var type by remember {
        mutableStateOf(oldTransaction?.type ?: TransactionType.EXPENSE)
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            Text(
                text = if (oldTransaction == null)
                    "Thêm giao dịch"
                else
                    "Sửa giao dịch",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text("Tên giao dịch")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                label = {
                    Text("Số tiền")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                },
                label = {
                    Text("Danh mục")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = date,
                onValueChange = {
                    date = it
                },
                label = {
                    Text("Ngày")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Button(
                    onClick = {
                        type = TransactionType.EXPENSE
                    }
                ) {
                    Text("Chi tiêu")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        type = TransactionType.INCOME
                    }
                ) {
                    Text("Thu nhập")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val money = amount.toDoubleOrNull() ?: 0.0

                    val transaction = Transaction(
                        id = oldTransaction?.id ?: 0,
                        title = title,
                        amount = money,
                        category = category,
                        type = type,
                        date = date,
                        walletId = oldTransaction?.walletId ?: 1
                    )

                    if (oldTransaction == null) {
                        repository.addTransaction(transaction)
                    } else {
                        repository.updateTransaction(transaction)
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quay lại")
            }
        }
    }
}