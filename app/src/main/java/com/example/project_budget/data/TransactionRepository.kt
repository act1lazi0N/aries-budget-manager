package com.example.project_budget.data

import com.example.project_budget.data.local.TransactionDao
import com.example.project_budget.data.local.toEntity
import com.example.project_budget.data.local.toModel
import com.example.project_budget.model.Budget
import com.example.project_budget.model.Category
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.Wallet

class TransactionRepository(
    private val transactionDao: TransactionDao? = null,
    initialTransactions: List<Transaction> = SampleData.transactions
) {
    private val transactions = initialTransactions.toMutableList()

    suspend fun getAllTransactions(): List<Transaction> {
        val dao = transactionDao ?: return transactions.toList()

        return dao.getAllTransactions().map { it.toModel() }
    }

    suspend fun getTransactionById(transactionId: Int): Transaction? {
        val dao = transactionDao
        if (dao != null) {
            return dao.getTransactionById(transactionId)?.toModel()
        }

        return transactions.firstOrNull { it.id == transactionId }
    }

    suspend fun addTransaction(transaction: Transaction): Transaction {
        val dao = transactionDao
        if (dao != null) {
            val transactionToAdd = if (
                transaction.id == 0 ||
                dao.getTransactionById(transaction.id) != null
            ) {
                transaction.copy(id = 0)
            } else {
                transaction
            }
            val insertedId = dao.insertTransaction(transactionToAdd.toEntity()).toInt()
            return transactionToAdd.copy(id = insertedId)
        }

        val transactionToAdd = if (transaction.id == 0 || getTransactionById(transaction.id) != null) {
            transaction.copy(id = nextId())
        } else {
            transaction
        }

        transactions.add(transactionToAdd)
        return transactionToAdd
    }

    suspend fun updateTransaction(transaction: Transaction): Boolean {
        val dao = transactionDao
        if (dao != null) {
            return dao.updateTransaction(transaction.toEntity()) > 0
        }

        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index == -1) return false

        transactions[index] = transaction
        return true
    }

    suspend fun deleteTransaction(transactionId: Int): Boolean {
        val dao = transactionDao
        if (dao != null) {
            return dao.deleteTransaction(transactionId) > 0
        }

        return transactions.removeIf { it.id == transactionId }
    }

    fun getCategories(): List<Category> = SampleData.categories

    fun getWallets(): List<Wallet> = SampleData.wallets

    fun getBudgets(): List<Budget> = SampleData.budgets

    private fun nextId(): Int {
        return (transactions.maxOfOrNull { it.id } ?: 0) + 1
    }
}
