package com.example.project_budget.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project_budget.data.SampleData

@Database(
    entities = [TransactionEntity::class],
    version = 2,
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
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(initialDataCallback)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val initialDataCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                SampleData.transactions.forEach { transaction ->
                    db.execSQL(
                        """
                            INSERT INTO transactions
                            (
                                id, title, amount, currency, convertedAmount,
                                convertedCurrency, exchangeRate, category, type,
                                date, note, walletId
                            )
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """.trimIndent(),
                        arrayOf<Any?>(
                            transaction.id,
                            transaction.title,
                            transaction.amount,
                            transaction.currency,
                            transaction.convertedAmount,
                            transaction.convertedCurrency,
                            transaction.exchangeRate,
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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN currency TEXT NOT NULL DEFAULT 'VND'")
                db.execSQL("ALTER TABLE transactions ADD COLUMN convertedAmount REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE transactions ADD COLUMN convertedCurrency TEXT NOT NULL DEFAULT 'VND'")
                db.execSQL("ALTER TABLE transactions ADD COLUMN exchangeRate REAL NOT NULL DEFAULT 1.0")
                db.execSQL("UPDATE transactions SET convertedAmount = amount WHERE convertedAmount = 0.0")
            }
        }
    }
}
