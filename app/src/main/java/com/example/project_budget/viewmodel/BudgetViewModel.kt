package com.example.project_budget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_budget.data.repository.TransactionRepository
import com.example.project_budget.data.TransactionTemp
import com.example.project_budget.data.local.AppDatabase
import com.example.project_budget.data.remote.FirebaseTransactionDataSource
import com.example.project_budget.data.repository.CurrencyRepository
import com.example.project_budget.model.Budget
import com.example.project_budget.model.DEFAULT_CURRENCY
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = TransactionRepository(
        transactionDao = AppDatabase.getInstance(application).transactionDao(),
        firebaseTransactionDataSource = FirebaseTransactionDataSource.createOrNull(application)
    )
    private val currencyRepository = CurrencyRepository()
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
        pullFirestoreTransactions()
        loadSupportedCurrencies()
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
            _uiState.value = createUiState(successMessage = "Đã thêm giao dịch.")
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val updated = repository.updateTransaction(transaction)
            _uiState.value = createUiState(
                successMessage = if (updated) "Đã cập nhật giao dịch." else null,
                errorMessage = if (updated) null else "Không tìm thấy giao dịch cần cập nhật."
            )
        }
    }

    fun addTransactionWithCurrencyConversion(
        transaction: Transaction,
        onSuccess: () -> Unit
    ) {
        saveTransactionWithCurrencyConversion(
            transaction = transaction,
            successMessage = "Đã thêm giao dịch.",
            onSuccess = onSuccess,
            save = repository::addTransaction
        )
    }

    fun updateTransactionWithCurrencyConversion(
        transaction: Transaction,
        onSuccess: () -> Unit
    ) {
        saveTransactionWithCurrencyConversion(
            transaction = transaction,
            successMessage = "Đã cập nhật giao dịch.",
            onSuccess = onSuccess,
            save = { updatedTransaction ->
                if (!repository.updateTransaction(updatedTransaction)) {
                    error("Không tìm thấy giao dịch cần cập nhật.")
                }
                updatedTransaction
            }
        )
    }

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            val deleted = repository.deleteTransaction(transactionId)
            _uiState.value = createUiState(
                successMessage = if (deleted) "Đã xóa giao dịch." else null,
                errorMessage = if (deleted) null else "Không tìm thấy giao dịch cần xóa."
            )
        }
    }

    fun importTransactions(importedTransactions: List<TransactionTemp>) {
        viewModelScope.launch {
            importedTransactions
                .map { it.toTransaction() }
                .forEach { transaction ->
                    repository.addTransaction(transaction)
                }

            _uiState.value = createUiState(
                successMessage = "Đã nhập ${importedTransactions.size} giao dịch."
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
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }

    private fun loadSupportedCurrencies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCurrencyLoading = true)
            val currencies = runCatching {
                currencyRepository.getSupportedCurrencies()
            }.getOrElse {
                DefaultSupportedCurrencies.toSet()
            }

            val visibleCurrencies = DefaultSupportedCurrencies
                .filter { it in currencies || it == DEFAULT_CURRENCY }
                .ifEmpty { DefaultSupportedCurrencies }

            _uiState.value = _uiState.value.copy(
                supportedCurrencies = visibleCurrencies,
                isCurrencyLoading = false
            )
        }
    }

    private fun saveTransactionWithCurrencyConversion(
        transaction: Transaction,
        successMessage: String,
        onSuccess: () -> Unit,
        save: suspend (Transaction) -> Transaction
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSavingTransaction = true,
                successMessage = null,
                errorMessage = null
            )

            runCatching {
                val convertedTransaction = convertTransactionAmount(transaction)
                save(convertedTransaction)
            }.fold(
                onSuccess = {
                    _uiState.value = createUiState(successMessage = successMessage)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSavingTransaction = false,
                        errorMessage = error.message
                            ?: "Không thể lấy tỷ giá. Vui lòng thử lại."
                    )
                }
            )
        }
    }

    private suspend fun convertTransactionAmount(transaction: Transaction): Transaction {
        val baseCurrency = transaction.currency.uppercase()
        val targetCurrency = _uiState.value.defaultCurrency.uppercase()
        val supportedCurrencies = _uiState.value.supportedCurrencies

        if (baseCurrency !in supportedCurrencies || targetCurrency !in supportedCurrencies) {
            error("Loại tiền này hiện chưa được hỗ trợ.")
        }

        // Get the exchange rate
        val rate = currencyRepository.getRate(
            fromCurrency = baseCurrency,
            toCurrency = targetCurrency
        )

        return transaction.copy(
            currency = rate.base,
            convertedAmount = transaction.amount * rate.rate, // To multiple with the base currency
            convertedCurrency = rate.quote,
            exchangeRate = rate.rate
        )
    }

    private fun refreshState(successMessage: String? = null) {
        viewModelScope.launch {
            _uiState.value = createUiState(successMessage = successMessage)
        }
    }

    private fun pullFirestoreTransactions() {
        viewModelScope.launch {
            val synced = repository.pullFirestoreTransactionsIntoRoom()
            repository.pushLocalTransactionsToFirestore()
            if (synced) {
                _uiState.value = createUiState()
            }
        }
    }

    private suspend fun createUiState(
        successMessage: String? = null,
        errorMessage: String? = null
    ): BudgetUiState {
        val transactions = repository.getAllTransactions()
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.convertedAmount }
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.convertedAmount }
        val expenseByCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.convertedAmount } }
        val categoryPercentages = calculateCategoryPercentages(expenseByCategory, totalExpense)
        val budgets = repository.getBudgets()
        val overBudgetCategories = findOverBudgetCategories(expenseByCategory, budgets)

        return BudgetUiState(
            transactions = transactions,
            categories = repository.getCategories(),
            wallets = repository.getWallets(),
            budgets = budgets,
            defaultCurrency = _uiState.value.defaultCurrency,
            supportedCurrencies = _uiState.value.supportedCurrencies,
            totalTransactions = transactions.size,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = totalIncome - totalExpense,
            averageAmount = if (transactions.isEmpty()) {
                0.0
            } else {
                transactions.sumOf { it.convertedAmount } / transactions.size
            },
            maxTransaction = transactions.maxByOrNull { it.convertedAmount },
            minTransaction = transactions.minByOrNull { it.convertedAmount },
            categoryStats = expenseByCategory,
            expenseByCategory = expenseByCategory,
            categoryPercentages = categoryPercentages,
            expenseTrend = buildExpenseTrend(transactions),
            overBudgetCategories = overBudgetCategories,
            overBudgetWarnings = buildOverBudgetWarnings(expenseByCategory, budgets),
            successMessage = successMessage,
            errorMessage = errorMessage
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

    private fun buildExpenseTrend(transactions: List<Transaction>): List<LineChartPoint> {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { transaction -> transaction.date.ifBlank { "Không ngày" } }
            .map { (date, transactionsByDate) ->
                LineChartPoint(
                    label = date,
                    amount = transactionsByDate.sumOf { it.convertedAmount }
                )
            }
            .sortedBy { point ->
                runCatching {
                    dateFormatter.parse(point.label)?.time
                }.getOrNull() ?: Long.MAX_VALUE
            }
    }

    private fun TransactionTemp.toTransaction(): Transaction {
        val transactionType = when (type.trim().lowercase()) {
            "income", "thu", "thu nhập", "thu nhap" -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }
        val trimmedCategory = category.ifBlank {
            if (transactionType == TransactionType.INCOME) "Thu nhập" else "Chi tiêu"
        }
        val wallet = _uiState.value.wallets.firstOrNull { it.name.equals(this.wallet, ignoreCase = true) }
        val title = note.ifBlank {
            "${transactionType.displayName} $trimmedCategory"
        }

        return Transaction(
            title = title,
            amount = amount,
            currency = currency.ifBlank { DEFAULT_CURRENCY }.uppercase(),
            convertedAmount = convertedAmount,
            convertedCurrency = convertedCurrency.ifBlank { DEFAULT_CURRENCY }.uppercase(),
            exchangeRate = exchangeRate,
            category = trimmedCategory,
            type = transactionType,
            date = date,
            note = note,
            walletId = wallet?.id ?: (_uiState.value.wallets.firstOrNull()?.id ?: 1)
        )
    }
}
