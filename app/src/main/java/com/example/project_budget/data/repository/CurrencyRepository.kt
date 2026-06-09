package com.example.project_budget.data.repository

import com.example.project_budget.data.remote.CurrencyApiService
import com.example.project_budget.data.remote.RetrofitClient
import com.example.project_budget.model.CurrencyRate

class CurrencyRepository(
    private val apiService: CurrencyApiService = RetrofitClient.currencyApiService
) {
    suspend fun getRate(
        fromCurrency: String,
        toCurrency: String
    ): CurrencyRate {
        val base = fromCurrency.uppercase()
        val quote = toCurrency.uppercase()

        if (base == quote) {
            return CurrencyRate(
                base = base,
                quote = quote,
                rate = 1.0,
                date = ""
            )
        }

        return apiService.getRate(base = base, quote = quote)
    }

    suspend fun getSupportedCurrencies(): Set<String> {
        return apiService.getCurrencies()
            .map { it.isoCode.uppercase() }
            .filter { it.isNotBlank() }
            .toSet()
    }
}
