package com.example.project_budget.model

const val DEFAULT_CURRENCY = "VND"

data class Transaction(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val currency: String = DEFAULT_CURRENCY,
    val convertedAmount: Double = amount,
    val convertedCurrency: String = DEFAULT_CURRENCY,
    val exchangeRate: Double = 1.0,
    val category: String,
    val type: TransactionType,
    val date: String = "",
    val note: String = "",
    val walletId: Int = 1
)
