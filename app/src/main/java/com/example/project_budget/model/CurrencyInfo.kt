package com.example.project_budget.model

import com.google.gson.annotations.SerializedName

data class CurrencyInfo(
    @SerializedName("iso_code")
    val isoCode: String = ""
)

