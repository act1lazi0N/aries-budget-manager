package com.example.project_budget.ui.navigation

import android.R.attr.type
import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_budget.ui.components.AppBottomBar
import com.example.project_budget.ui.screen.home.HomeScreen
import com.example.project_budget.ui.screen.transaction.TransactionListScreen
import com.example.project_budget.viewmodel.BudgetViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: BudgetViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onAddTransactionClick = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onViewAllTransactionsClick = {
                        navController.navigate(Screen.Transactions.route)
                    }
                )
            }

            composable(Screen.Transactions.route) {
                TransactionListScreen(
                    uiState = uiState,
                    onAddTransactionClick = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onTransactionClick = { transactionId ->
                        navController.navigate(Screen.editRoute(transactionId))
                    },
                    onDeleteClick = viewModel::deleteTransaction
                )
            }

            composable(Screen.AddTransaction.route) {
                PlaceholderScreen(
                    title = "Thêm giao dịch",
                    actionText = "Quay lại",
                    paddingValues = PaddingValues(),
                    onActionClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
            ) { entry ->
                val transactionId = entry.arguments?.getInt("transactionId") ?: 0
                PlaceholderScreen(
                    title = "Sửa giao dịch #$transactionId",
                    actionText = "Quay lại",
                    paddingValues = PaddingValues(),
                    onActionClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Statistics.route) {
                PlaceholderScreen(title = "Thống kê", paddingValues = PaddingValues())
            }

            composable(Screen.Settings.route) {
                PlaceholderScreen(title = "Cài đặt", paddingValues = PaddingValues())
            }

            composable(Screen.About.route) {
                PlaceholderScreen(title = "Giới thiệu", paddingValues = PaddingValues())
            }
        }
    }
}

private val bottomBarRoutes = setOf(
    Screen.Home.route,
    Screen.Transactions.route,
    Screen.Statistics.route,
    Screen.Settings.route
)

@Composable
private fun PlaceholderScreen(
    title: String,
    paddingValues: PaddingValues,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
        if (actionText != null && onActionClick != null) {
            Button(
                onClick = onActionClick,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = actionText)
            }
        }
    }
}
