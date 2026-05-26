package com.example.project_budget.data

import com.example.project_budget.model.Budget
import com.example.project_budget.model.Category
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.Wallet

class TransactionRepository(
    initialTransactions: List<Transaction> = SampleData.transactions
) {
    private val transactions = initialTransactions.toMutableList()

    fun getAllTransactions(): List<Transaction> = transactions.toList()

    fun getTransactionById(transactionId: Int): Transaction? {
        return transactions.firstOrNull { it.id == transactionId }
    }

    fun addTransaction(transaction: Transaction): Transaction {
        val transactionToAdd = if (transaction.id == 0 || getTransactionById(transaction.id) != null) {
            transaction.copy(id = nextId())
        } else {
            transaction
        }

        transactions.add(transactionToAdd)
        return transactionToAdd
    }

    fun updateTransaction(transaction: Transaction): Boolean {
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index == -1) return false

        transactions[index] = transaction
        return true
    }

    fun deleteTransaction(transactionId: Int): Boolean {
        return transactions.removeIf { it.id == transactionId }
    }

    fun getCategories(): List<Category> = SampleData.categories

    fun getWallets(): List<Wallet> = SampleData.wallets

    fun getBudgets(): List<Budget> = SampleData.budgets

    private fun nextId(): Int {
        return (transactions.maxOfOrNull { it.id } ?: 0) + 1
    }
}
