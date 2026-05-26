package com.example.project_budget.ui.navigation

sealed class Screen(
    val route: String,
    val label: String,
    val shortLabel: String
) {
    data object Home : Screen("home", "Trang chủ", "TC")
    data object Transactions : Screen("transactions", "Giao dịch", "GD")
    data object AddTransaction : Screen("add_transaction", "Thêm", "+")
    data object EditTransaction : Screen("edit_transaction/{transactionId}", "Sửa", "S")
    data object Statistics : Screen("statistics", "Thống kê", "TK")
    data object Settings : Screen("settings", "Cài đặt", "CĐ")
    data object About : Screen("about", "Giới thiệu", "GT")

    companion object {
        fun editRoute(transactionId: Int): String {
            return "edit_transaction/$transactionId"
        }
    }

    fun routeWithId(transactionId: Int): String {
        return "edit_transaction/$transactionId"
    }
}
