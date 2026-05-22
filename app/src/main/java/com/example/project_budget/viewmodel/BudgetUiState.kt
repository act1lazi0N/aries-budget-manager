package com.example.project_budget.viewmodel

import com.example.project_budget.model.Budget
import com.example.project_budget.model.Category
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.Wallet

data class BudgetUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val totalTransactions: Int = 0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val averageAmount: Double = 0.0,
    val maxTransaction: Transaction? = null,
    val minTransaction: Transaction? = null,
    val categoryStats: Map<String, Double> = emptyMap(),
    val expenseByCategory: Map<String, Double> = emptyMap(),
    val categoryPercentages: Map<String, Double> = emptyMap(),
    val overBudgetCategories: List<String> = emptyList(),
    val overBudgetWarnings: List<String> = emptyList(),
    val errorMessage: String? = null
)
