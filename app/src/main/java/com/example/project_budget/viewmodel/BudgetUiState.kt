package com.example.project_budget.viewmodel

import com.example.project_budget.model.Budget
import com.example.project_budget.model.Category
import com.example.project_budget.model.DEFAULT_CURRENCY
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.Wallet

val DefaultSupportedCurrencies = listOf(DEFAULT_CURRENCY, "USD", "EUR", "JPY", "KRW")

data class LineChartPoint(
    val label: String,
    val amount: Double
)

data class BudgetUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val defaultCurrency: String = DEFAULT_CURRENCY,
    val supportedCurrencies: List<String> = DefaultSupportedCurrencies,
    val isCurrencyLoading: Boolean = false,
    val isSavingTransaction: Boolean = false,
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
    val expenseTrend: List<LineChartPoint> = emptyList(),
    val overBudgetCategories: List<String> = emptyList(),
    val overBudgetWarnings: List<String> = emptyList(),
    val successMessage: String? = null,
    val errorMessage: String? = null
)
