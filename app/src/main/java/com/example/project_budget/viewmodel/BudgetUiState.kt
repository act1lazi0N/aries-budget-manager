package com.example.project_budget.viewmodel

import com.example.project_budget.model.Transaction

data class BudgetUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val averageAmount: Double = 0.0,
    val maxTransaction: Transaction? = null,
    val minTransaction: Transaction? = null,
    val categoryStats: Map<String, Double> = emptyMap(),
    val categoryPercentages: Map<String, Double> = emptyMap(),
    val errorMessage: String? = null
)