package com.example.project_budget.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val currency: String,
    val convertedAmount: Double,
    val convertedCurrency: String,
    val exchangeRate: Double,
    val category: String,
    val type: String,
    val date: String,
    val note: String,
    val walletId: Int
)

fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = id,
        title = title,
        amount = amount,
        currency = currency,
        convertedAmount = convertedAmount,
        convertedCurrency = convertedCurrency,
        exchangeRate = exchangeRate,
        category = category,
        type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.EXPENSE),
        date = date,
        note = note,
        walletId = walletId
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        currency = currency,
        convertedAmount = convertedAmount,
        convertedCurrency = convertedCurrency,
        exchangeRate = exchangeRate,
        category = category,
        type = type.name,
        date = date,
        note = note,
        walletId = walletId
    )
}
