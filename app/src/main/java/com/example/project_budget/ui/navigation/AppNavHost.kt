package com.example.project_budget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_budget.data.TransactionRepository
import com.example.project_budget.ui.screen.home.HomeScreen
import com.example.project_budget.ui.screen.transaction.AddEditTransactionScreen
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
                onEditTransactionClick = { transaction ->
                    navController.navigate("edit_transaction/${transaction.id}")
                },
                onBottomNavClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Transactions.route) {
            TransactionListScreen(
                transactions = repository.getAllTransactions(),
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
            AddEditTransactionScreen(
                navController = navController,
                repository = repository,
                transactionId = null
            )
        }

        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId")
            AddEditTransactionScreen(
                navController = navController,
                repository = repository,
                transactionId = transactionId
            )
        }
    }
}