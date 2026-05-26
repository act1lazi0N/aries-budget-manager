package com.example.project_budget.model

data class Transaction(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val date: String = "",
    val note: String = "",
    val walletId: Int = 1
)
