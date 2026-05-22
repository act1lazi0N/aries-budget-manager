package com.example.project_budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.project_budget.ui.navigation.AppNavHost
import com.example.project_budget.ui.theme.Project_BudgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Project_BudgetTheme {
                AppNavHost()
            }
        }
    }
}