package com.example.project_budget.model

data class Wallet(
    val id: Int,
    val name: String,
    val balance: Double,
    val currency: String = "VND"
)
