package com.example.project_budget.data

import com.example.project_budget.model.Budget
import com.example.project_budget.model.Category
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.model.Wallet

object SampleData {
    val categories = listOf(
        Category(id = 1, name = "Ăn uống", type = TransactionType.EXPENSE),
        Category(id = 2, name = "Di chuyển", type = TransactionType.EXPENSE),
        Category(id = 3, name = "Mua sắm", type = TransactionType.EXPENSE),
        Category(id = 4, name = "Giải trí", type = TransactionType.EXPENSE),
        Category(id = 5, name = "Lương", type = TransactionType.INCOME),
        Category(id = 6, name = "Thưởng", type = TransactionType.INCOME)
    )

    val wallets = listOf(
        Wallet(id = 1, name = "Tiền mặt", balance = 2_000_000.0),
        Wallet(id = 2, name = "Tài khoản ngân hàng", balance = 5_000_000.0)
    )

    val budgets = listOf(
        Budget(id = 1, category = "Ăn uống", limitAmount = 2_000_000.0),
        Budget(id = 2, category = "Di chuyển", limitAmount = 800_000.0),
        Budget(id = 3, category = "Mua sắm", limitAmount = 1_500_000.0),
        Budget(id = 4, category = "Giải trí", limitAmount = 700_000.0)
    )

    val transactions = listOf(
        Transaction(
            id = 1,
            title = "Lương tháng",
            amount = 8_000_000.0,
            category = "Lương",
            type = TransactionType.INCOME,
            date = "2026-05-01",
            walletId = 2
        ),
        Transaction(
            id = 2,
            title = "Ăn trưa",
            amount = 55_000.0,
            category = "Ăn uống",
            type = TransactionType.EXPENSE,
            date = "2026-05-02"
        ),
        Transaction(
            id = 3,
            title = "Xăng xe",
            amount = 90_000.0,
            category = "Di chuyển",
            type = TransactionType.EXPENSE,
            date = "2026-05-03"
        ),
        Transaction(
            id = 4,
            title = "Cà phê",
            amount = 45_000.0,
            category = "Ăn uống",
            type = TransactionType.EXPENSE,
            date = "2026-05-04"
        )
    )
}
