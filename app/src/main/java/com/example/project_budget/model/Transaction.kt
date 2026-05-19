package com.example.project_budget.model

data class Transaction (
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val date: String,
    val note: String = ""
)