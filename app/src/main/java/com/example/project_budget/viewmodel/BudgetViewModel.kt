package com.example.project_budget.viewmodel

import androidx.lifecycle.ViewModel
import com.example.project_budget.data.TransactionRepository
import com.example.project_budget.model.Budget
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BudgetViewModel(
    private val repository: TransactionRepository = TransactionRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(createUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        repository.addTransaction(transaction)
        refreshState(message = "Đã thêm giao dịch.")
    }

    fun updateTransaction(transaction: Transaction) {
        val updated = repository.updateTransaction(transaction)
        refreshState(
            message = if (updated) {
                "Đã cập nhật giao dịch."
            } else {
                "Không tìm thấy giao dịch cần cập nhật."
            }
        )
    }

    fun deleteTransaction(transactionId: Int) {
        val deleted = repository.deleteTransaction(transactionId)
        refreshState(
            message = if (deleted) {
                "Đã xóa giao dịch."
            } else {
                "Không tìm thấy giao dịch cần xóa."
            }
        )
    }

    fun getTransactionById(transactionId: Int): Transaction? {
        return repository.getTransactionById(transactionId)
    }

    fun validateTransaction(title: String, amountText: String, category: String): String? {
        if (title.isBlank()) {
            return "Vui lòng nhập tên giao dịch."
        }

        val amount = amountText.toDoubleOrNull()
            ?: return "Vui lòng nhập số tiền hợp lệ."

        if (amount <= 0.0) {
            return "Số tiền phải lớn hơn 0."
        }

        if (category.isBlank()) {
            return "Vui lòng chọn danh mục."
        }

        return null
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun refreshState(message: String? = null) {
        _uiState.value = createUiState(message)
    }

    private fun createUiState(message: String? = null): BudgetUiState {
        val transactions = repository.getAllTransactions()
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val categoryTotals = calculateCategoryTotals(transactions)
        val expenseByCategory = calculateExpenseByCategory(transactions)
        val categoryPercentages = calculateCategoryPercentages(expenseByCategory, totalExpense)
        val budgets = repository.getBudgets()
        val overBudgetCategories = findOverBudgetCategories(expenseByCategory, budgets)

        return BudgetUiState(
            transactions = transactions,
            categories = repository.getCategories(),
            wallets = repository.getWallets(),
            budgets = budgets,
            totalTransactions = transactions.size,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = totalIncome - totalExpense,
            averageAmount = if (transactions.isEmpty()) {
                0.0
            } else {
                transactions.sumOf { it.amount } / transactions.size
            },
            maxTransaction = transactions.maxByOrNull { it.amount },
            minTransaction = transactions.minByOrNull { it.amount },
            categoryTotals = categoryTotals,
            categoryStats = expenseByCategory,
            expenseByCategory = expenseByCategory,
            categoryPercentages = categoryPercentages,
            overBudgetCategories = overBudgetCategories,
            overBudgetWarnings = buildOverBudgetWarnings(expenseByCategory, budgets),
            errorMessage = message
        )
    }

    private fun calculateCategoryTotals(transactions: List<Transaction>): Map<String, Double> {
        return transactions
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    private fun calculateExpenseByCategory(transactions: List<Transaction>): Map<String, Double> {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    private fun calculateCategoryPercentages(
        expenseByCategory: Map<String, Double>,
        totalExpense: Double
    ): Map<String, Double> {
        if (totalExpense <= 0.0) return emptyMap()

        return expenseByCategory.mapValues { (_, amount) ->
            amount / totalExpense * 100
        }
    }

    private fun findOverBudgetCategories(
        expenseByCategory: Map<String, Double>,
        budgets: List<Budget>
    ): List<String> {
        return budgets
            .filter { budget ->
                val spent = expenseByCategory[budget.category] ?: 0.0
                spent > budget.limitAmount
            }
            .map { it.category }
    }

    private fun buildOverBudgetWarnings(
        expenseByCategory: Map<String, Double>,
        budgets: List<Budget>
    ): List<String> {
        return budgets.mapNotNull { budget ->
            val spent = expenseByCategory[budget.category] ?: 0.0
            if (spent > budget.limitAmount) {
                "Danh mục ${budget.category} đã vượt ngân sách."
            } else {
                null
            }
        }
    }
}
