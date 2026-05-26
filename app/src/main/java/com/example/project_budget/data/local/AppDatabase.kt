package com.example.project_budget.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project_budget.data.SampleData

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aries_budget.db"
                ).addCallback(initialDataCallback).build().also { INSTANCE = it }
            }
        }

        private val initialDataCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                SampleData.transactions.forEach { transaction ->
                    db.execSQL(
                        """
                            INSERT INTO transactions
                            (id, title, amount, category, type, date, note, walletId)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """.trimIndent(),
                        arrayOf(
                            transaction.id,
                            transaction.title,
                            transaction.amount,
                            transaction.category,
                            transaction.type.name,
                            transaction.date,
                            transaction.note,
                            transaction.walletId
                        )
                    )
                }
            }
        }
    }
}
