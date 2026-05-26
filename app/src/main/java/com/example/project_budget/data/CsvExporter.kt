package com.example.project_budget.data

data class TransactionTemp(
    val amount: Double,
    val type: String,
    val category: String,
    val wallet: String,
    val date: String,
    val note: String
)

object CsvExporter {
    fun exportToCsv(transactions: List<TransactionTemp>): String {
        return buildString {
            append("Số tiền,Loại,Danh mục,Ví,Ngày,Ghi chú\n")
            transactions.forEach { t ->
                append("${t.amount},${t.type},${t.category},${t.wallet},${t.date},${t.note}\n")
            }
        }
    }

    fun importFromCsv(csvString: String): List<TransactionTemp> {
        val list = mutableListOf<TransactionTemp>()
        val lines = csvString.lines()

        if (lines.size > 1) {
            for (i in 1 until lines.size) {
                val cols = lines[i].split(",")
                if (cols.size >= 6) {
                    val amount = cols[0].toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        list.add(TransactionTemp(amount, cols[1], cols[2], cols[3], cols[4], cols[5]))
                    }
                }
            }
        }
        return list
    }
}