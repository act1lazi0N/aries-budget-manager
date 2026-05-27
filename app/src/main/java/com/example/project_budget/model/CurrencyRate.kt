package com.example.project_budget.model

data class CurrencyRate(
    val base: String,
    val quote: String,
    val rate: Double,
    val date: String
)

