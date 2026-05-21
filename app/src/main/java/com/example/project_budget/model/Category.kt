package com.example.project_budget.model

data class Category(
    val id: Int,
    val name: String,
    val icon: String,
    val type: TransactionType
)