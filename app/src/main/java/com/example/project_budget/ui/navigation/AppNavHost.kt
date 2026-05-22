package com.example.project_budget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_budget.data.TransactionRepository
import com.example.project_budget.ui.screen.home.HomeScreen
import com.example.project_budget.ui.screen.transaction.AddEditTransactionScreen
import com.example.project_budget.ui.screen.transaction.TransactionListScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val repository = remember {
        TransactionRepository()
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }

        composable("transaction_list") {
            TransactionListScreen(
                navController = navController,
                repository = repository
            )
        }

        composable("add_transaction") {
            AddEditTransactionScreen(
                navController = navController,
                repository = repository,
                transactionId = null
            )
        }

        composable("edit_transaction/{id}") { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull()

            AddEditTransactionScreen(
                navController = navController,
                repository = repository,
                transactionId = id
            )
        }
    }
}