package com.example.project_budget.data

data class TransactionTemp(
    val amount: Double,
    val type: String,
    val category: String,
    val wallet: String,
    val date: String,
    val note: String,
    val currency: String = "VND",
    val convertedAmount: Double = amount,
    val convertedCurrency: String = "VND",
    val exchangeRate: Double = 1.0
)

object CsvExporter {
    fun exportToCsv(transactions: List<TransactionTemp>): String {
        return buildString {
            append("So tien,Tien te,So tien quy doi,Tien te quy doi,Ty gia,Loai,Danh muc,Vi,Ngay,Ghi chu\n")
            transactions.forEach { t ->
                append("${t.amount},${t.currency},${t.convertedAmount},${t.convertedCurrency},${t.exchangeRate},${t.type},${t.category},${t.wallet},${t.date},${t.note}\n")
            }
        }
    }

    fun importFromCsv(csvString: String): List<TransactionTemp> {
        val list = mutableListOf<TransactionTemp>()
        val lines = csvString.lines()

        if (lines.size > 1) {
            for (i in 1 until lines.size) {
                val cols = lines[i].split(",")
                if (cols.size >= 10) {
                    val amount = cols[0].toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        list.add(
                            TransactionTemp(
                                amount = amount,
                                type = cols[5],
                                category = cols[6],
                                wallet = cols[7],
                                date = cols[8],
                                note = cols[9],
                                currency = cols[1].ifBlank { "VND" },
                                convertedAmount = cols[2].toDoubleOrNull() ?: amount,
                                convertedCurrency = cols[3].ifBlank { "VND" },
                                exchangeRate = cols[4].toDoubleOrNull() ?: 1.0
                            )
                        )
                    }
                } else if (cols.size >= 6) {
                    val amount = cols[0].toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        list.add(
                            TransactionTemp(
                                amount = amount,
                                type = cols[1],
                                category = cols[2],
                                wallet = cols[3],
                                date = cols[4],
                                note = cols[5]
                            )
                        )
                    }
                }
            }
        }
        return list
    }
}

