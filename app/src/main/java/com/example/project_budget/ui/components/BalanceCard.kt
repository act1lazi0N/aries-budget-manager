package com.example.project_budget.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_budget.utils.formatCurrency

@Composable
fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text("Số dư hiện tại", color = MaterialTheme.colorScheme.onSurface)

            Text(
                text = "${formatCurrency(balance)} ₫",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Column {
                    Text("Thu nhập", color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "+${formatCurrency(income)} ₫",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text("Chi tiêu", color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "-${formatCurrency(expense)} ₫",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }
    }
}