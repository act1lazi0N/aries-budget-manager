package com.example.project_budget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_budget.data.TransactionRepository
import com.example.project_budget.data.local.AppDatabase
import com.example.project_budget.model.Budget
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = TransactionRepository(
        transactionDao = AppDatabase.getInstance(application).transactionDao()
    )
    private val _uiState = MutableStateFlow(
        BudgetUiState(
            categories = repository.getCategories(),
            wallets = repository.getWallets(),
            budgets = repository.getBudgets()
        )
    )
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        refreshState()
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
            _uiState.value = createUiState(message = "Đã thêm giao dịch.")
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val updated = repository.updateTransaction(transaction)
            _uiState.value = createUiState(
                message = if (updated) {
                    "Đã cập nhật giao dịch."
                } else {
                    "Không tìm thấy giao dịch cần cập nhật."
                }
            )
        }
    }

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            val deleted = repository.deleteTransaction(transactionId)
            _uiState.value = createUiState(
                message = if (deleted) {
                    "Đã xóa giao dịch."
                } else {
                    "Không tìm thấy giao dịch cần xóa."
                }
            )
        }
    }

    fun getTransactionById(transactionId: Int): Transaction? {
        return _uiState.value.transactions.firstOrNull { it.id == transactionId }
    }

    fun validateTransaction(
        title: String,
        amountText: String,
        category: String,
        note: String = ""
    ): String? {
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

        if (note.length > 200) {
            return "Ghi chú không được vượt quá 200 ký tự."
        }

        return null
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun refreshState(message: String? = null) {
        viewModelScope.launch {
            _uiState.value = createUiState(message)
        }
    }

    private suspend fun createUiState(message: String? = null): BudgetUiState {
        val transactions = repository.getAllTransactions()
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val expenseByCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
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
            categoryStats = expenseByCategory,
            expenseByCategory = expenseByCategory,
            categoryPercentages = categoryPercentages,
            overBudgetCategories = overBudgetCategories,
            overBudgetWarnings = buildOverBudgetWarnings(expenseByCategory, budgets),
            errorMessage = message
        )
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
