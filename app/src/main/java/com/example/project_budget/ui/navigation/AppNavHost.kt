package com.example.project_budget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_budget.data.TransactionRepository
import com.example.project_budget.ui.screen.home.HomeScreen
import com.example.project_budget.ui.screen.transaction.TransactionListScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val repository = remember { TransactionRepository() }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                transactions = repository.getAllTransactions(),
                onAddTransactionClick = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onViewAllTransactionsClick = {
                    navController.navigate(Screen.Transactions.route)
                },
                onBottomNavClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Transactions.route) {
            TransactionListScreen(
                transactions = repository.getAllTransactions(),
                onBackClick = {
                    navController.popBackStack()
                },
                onAddTransactionClick = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onTransactionClick = { id ->
                    navController.navigate("edit_transaction/$id")
                },
                onDeleteClick = { },
                onBottomNavClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.AddTransaction.route) {
            TransactionListScreen(
                transactions = repository.getAllTransactions(),
                onBackClick = { navController.popBackStack() },
                onAddTransactionClick = { },
                onTransactionClick = { },
                onDeleteClick = { },
                onBottomNavClick = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.EditTransaction.route) {
            TransactionListScreen(
                transactions = repository.getAllTransactions(),
                onBackClick = { navController.popBackStack() },
                onAddTransactionClick = { },
                onTransactionClick = { },
                onDeleteClick = { },
                onBottomNavClick = { route -> navController.navigate(route) }
            )
        }
    }
}