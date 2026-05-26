package com.example.project_budget.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault())
    symbols.groupingSeparator = '.'
    val decimalFormat = DecimalFormat("#,###", symbols)
    return decimalFormat.format(amount)
}
