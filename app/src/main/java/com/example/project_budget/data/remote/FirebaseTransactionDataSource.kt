package com.example.project_budget.data.remote

import android.content.Context
import com.example.project_budget.model.DEFAULT_CURRENCY
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private const val DEMO_USER_ID = "demo-user"

class FirebaseTransactionDataSource private constructor(
    private val firestore: FirebaseFirestore,
    private val userId: String = DEMO_USER_ID
) {
    suspend fun uploadTransaction(transaction: Transaction) {
        userTransactions()
            .document(transaction.id.toString())
            .set(transaction.toFirestoreMap())
            .await()
    }

    suspend fun deleteTransaction(transactionId: Int) {
        userTransactions()
            .document(transactionId.toString())
            .delete()
            .await()
    }

    suspend fun getTransactions(): List<Transaction> {
        return userTransactions()
            .get()
            .await()
            .documents
            .mapNotNull { document -> document.toTransaction() }
    }

    private fun userTransactions(): CollectionReference {
        return firestore.collection("users")
            .document(userId)
            .collection("transactions")
    }

    private fun Transaction.toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "amount" to amount,
            "currency" to currency,
            "convertedAmount" to convertedAmount,
            "convertedCurrency" to convertedCurrency,
            "exchangeRate" to exchangeRate,
            "category" to category,
            "type" to type.name,
            "date" to date,
            "note" to note,
            "walletId" to walletId,
            "updatedAt" to FieldValue.serverTimestamp(),
            "deleted" to false
        )
    }

    private fun DocumentSnapshot.toTransaction(): Transaction? {
        val data = data ?: return null

        return data.toTransaction(id.toIntOrNull())
    }

    private fun Map<String, Any>.toTransaction(fallbackId: Int?): Transaction? {
        if (this["deleted"] == true) return null

        val id = (this["id"] as? Number)?.toInt() ?: fallbackId ?: return null
        val title = this["title"] as? String ?: return null
        val amount = (this["amount"] as? Number)?.toDouble() ?: return null
        val category = this["category"] as? String ?: return null
        val typeName = this["type"] as? String ?: return null
        val type = runCatching { TransactionType.valueOf(typeName) }.getOrNull() ?: return null

        return Transaction(
            id = id,
            title = title,
            amount = amount,
            currency = this["currency"] as? String ?: DEFAULT_CURRENCY,
            convertedAmount = (this["convertedAmount"] as? Number)?.toDouble() ?: amount,
            convertedCurrency = this["convertedCurrency"] as? String ?: DEFAULT_CURRENCY,
            exchangeRate = (this["exchangeRate"] as? Number)?.toDouble() ?: 1.0,
            category = category,
            type = type,
            date = this["date"] as? String ?: "",
            note = this["note"] as? String ?: "",
            walletId = (this["walletId"] as? Number)?.toInt() ?: 1
        )
    }

    companion object {
        fun createOrNull(context: Context): FirebaseTransactionDataSource? {
            val app = FirebaseApp.initializeApp(context.applicationContext) ?: return null
            return FirebaseTransactionDataSource(
                firestore = FirebaseFirestore.getInstance(app)
            )
        }
    }
}
