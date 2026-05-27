package com.example.project_budget.data.remote

import com.example.project_budget.model.CurrencyInfo
import com.example.project_budget.model.CurrencyRate
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v2/rate/{base}/{quote}")
    suspend fun getRate(
        @Path("base") base: String,
        @Path("quote") quote: String
    ): CurrencyRate

    @GET("v2/currencies")
    suspend fun getCurrencies(): List<CurrencyInfo>
}
