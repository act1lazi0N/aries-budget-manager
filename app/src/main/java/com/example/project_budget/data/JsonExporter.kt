package com.example.project_budget.data

import org.json.JSONArray
import org.json.JSONObject

object JsonExporter {
    fun exportToJson(transactions: List<TransactionTemp>): String {
        val jsonArray = JSONArray()
        transactions.forEach { t ->
            val jsonObj = JSONObject().apply {
                put("amount", t.amount)
                put("currency", t.currency)
                put("convertedAmount", t.convertedAmount)
                put("convertedCurrency", t.convertedCurrency)
                put("exchangeRate", t.exchangeRate)
                put("type", t.type)
                put("category", t.category)
                put("wallet", t.wallet)
                put("date", t.date)
                put("note", t.note)
            }
            jsonArray.put(jsonObj)
        }
        return jsonArray.toString(4)
    }

    fun importFromJson(jsonString: String): List<TransactionTemp> {
        val list = mutableListOf<TransactionTemp>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val amount = obj.optDouble("amount", 0.0)

                if (amount > 0) {
                    list.add(
                        TransactionTemp(
                            amount = amount,
                            type = obj.optString("type", "Chi"),
                            category = obj.optString("category", ""),
                            wallet = obj.optString("wallet", ""),
                            date = obj.optString("date", ""),
                            note = obj.optString("note", ""),
                            currency = obj.optString("currency", "VND"),
                            convertedAmount = obj.optDouble("convertedAmount", amount),
                            convertedCurrency = obj.optString("convertedCurrency", "VND"),
                            exchangeRate = obj.optDouble("exchangeRate", 1.0)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
