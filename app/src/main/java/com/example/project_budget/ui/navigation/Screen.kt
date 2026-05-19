package com.example.project_budget.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Transactions : Screen("transactions")
    data object AddTransaction : Screen("add_transaction")
    data object EditTransaction : Screen("edit_transaction/{transactionId}")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object About : Screen("about")

    fun editRoute(transactionId: Int): String {
        return "edit_transaction/$transactionId"
    }
}