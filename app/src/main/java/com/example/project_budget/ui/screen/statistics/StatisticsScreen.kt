package com.example.project_budget.ui.screen.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_budget.model.Transaction
import com.example.project_budget.model.TransactionType
import com.example.project_budget.ui.components.CategoryStatItem
import com.example.project_budget.ui.components.EmptyState
import com.example.project_budget.ui.components.StatCard
import com.example.project_budget.ui.components.formatMoney
import com.example.project_budget.ui.theme.Project_BudgetTheme
import com.example.project_budget.viewmodel.BudgetUiState
import com.example.project_budget.viewmodel.LineChartPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    uiState: BudgetUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Thống kê") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle(text = "Tổng quan")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Giao dịch",
                    value = uiState.totalTransactions.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Số dư",
                    value = formatMoney(uiState.balance),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Tổng thu",
                    value = formatMoney(uiState.totalIncome),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tổng chi",
                    value = formatMoney(uiState.totalExpense),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Trung bình",
                    value = formatMoney(uiState.averageAmount),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Lớn nhất",
                    value = uiState.maxTransaction?.let { formatTransactionSummary(it) } ?: "-",
                    modifier = Modifier.weight(1f)
                )
            }
            StatCard(
                title = "Nhỏ nhất",
                value = uiState.minTransaction?.let { formatTransactionSummary(it) } ?: "-"
            )

            SectionTitle(text = "Biểu đồ")
            ExpenseLineChart(points = uiState.expenseTrend)
            CategoryPieChart(
                categoryAmounts = uiState.expenseByCategory,
                categoryPercentages = uiState.categoryPercentages
            )

            SectionTitle(text = "Theo danh mục")
            if (uiState.expenseByCategory.isEmpty()) {
                EmptyState(
                    title = "Chưa có dữ liệu chi tiêu",
                    message = "Thêm giao dịch chi để xem thống kê theo danh mục."
                )
            } else {
                uiState.expenseByCategory.forEach { (category, amount) ->
                    CategoryStatItem(
                        category = category,
                        amount = amount,
                        percentage = uiState.categoryPercentages[category] ?: 0.0
                    )
                }
            }

            SectionTitle(text = "Cảnh báo ngân sách")
            if (uiState.overBudgetWarnings.isEmpty()) {
                StatCard(
                    title = "Trạng thái",
                    value = "Chưa có danh mục vượt ngân sách"
                )
            } else {
                uiState.overBudgetWarnings.forEach { warning ->
                    StatCard(
                        title = "Vượt ngân sách",
                        value = warning
                    )
                }
            }
        }
    }
}

private fun formatTransactionSummary(transaction: Transaction): String {
    return "${transaction.title} - ${formatMoney(transaction.convertedAmount)}"
}

@Composable
private fun ExpenseLineChart(points: List<LineChartPoint>) {
    val axisColor = MaterialTheme.colorScheme.outlineVariant
    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.tertiary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Biểu đồ đường chi tiêu",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Xu hướng tổng chi theo ngày",
                color = labelColor,
                style = MaterialTheme.typography.bodyMedium
            )

            if (points.isEmpty()) {
                ChartEmptyMessage(text = "Chưa có giao dịch chi để vẽ biểu đồ.")
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val chartLeft = 12.dp.toPx()
                    val chartTop = 12.dp.toPx()
                    val chartRight = size.width - 12.dp.toPx()
                    val chartBottom = size.height - 24.dp.toPx()
                    val chartWidth = chartRight - chartLeft
                    val chartHeight = chartBottom - chartTop
                    val maxAmount = points.maxOfOrNull { it.amount }?.takeIf { it > 0.0 } ?: 1.0

                    drawLine(
                        color = axisColor,
                        start = Offset(chartLeft, chartBottom),
                        end = Offset(chartRight, chartBottom),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = axisColor,
                        start = Offset(chartLeft, chartTop),
                        end = Offset(chartLeft, chartBottom),
                        strokeWidth = 1.dp.toPx()
                    )

                    val offsets = points.mapIndexed { index, point ->
                        val x = if (points.size == 1) {
                            chartLeft + chartWidth / 2
                        } else {
                            chartLeft + chartWidth * index / (points.size - 1)
                        }
                        val y = chartBottom - (point.amount / maxAmount).toFloat() * chartHeight
                        Offset(x, y)
                    }

                    val path = Path().apply {
                        offsets.forEachIndexed { index, offset ->
                            if (index == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    offsets.forEach { offset ->
                        drawCircle(
                            color = pointColor,
                            radius = 4.dp.toPx(),
                            center = offset
                        )
                    }
                }
                Text(
                    text = "Cao nhất: ${formatMoney(points.maxOf { it.amount })}",
                    color = labelColor,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Từ ${points.first().label} đến ${points.last().label}",
                    color = labelColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun CategoryPieChart(
    categoryAmounts: Map<String, Double>,
    categoryPercentages: Map<String, Double>
) {
    val chartColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val entries = categoryAmounts.entries
        .filter { it.value > 0.0 }
        .sortedByDescending { it.value }
    val total = entries.sumOf { it.value }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Biểu đồ tròn danh mục",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tỷ trọng chi tiêu theo danh mục",
                color = labelColor,
                style = MaterialTheme.typography.bodyMedium
            )

            if (entries.isEmpty() || total <= 0.0) {
                ChartEmptyMessage(text = "Chưa có dữ liệu danh mục để vẽ biểu đồ.")
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Canvas(modifier = Modifier.size(160.dp)) {
                        var startAngle = -90f
                        entries.forEachIndexed { index, entry ->
                            val sweepAngle = (entry.value / total * 360.0).toFloat()
                            drawArc(
                                color = chartColors[index % chartColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset.Zero,
                                size = Size(size.width, size.height)
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        entries.take(5).forEachIndexed { index, entry ->
                            PieLegendItem(
                                color = chartColors[index % chartColors.size],
                                label = entry.key,
                                percentage = categoryPercentages[entry.key] ?: 0.0
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PieLegendItem(
    color: Color,
    label: String,
    percentage: Double
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label (${percentage.toInt()}%)",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ChartEmptyMessage(text: String) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview(showBackground = true, widthDp = 420, heightDp = 1500)
@Composable
private fun StatisticsScreenPreview() {
    Project_BudgetTheme {
        StatisticsScreen(
            uiState = BudgetUiState(
                transactions = listOf(
                    Transaction(
                        id = 1,
                        title = "Lương tháng",
                        amount = 8_000_000.0,
                        category = "Lương",
                        type = TransactionType.INCOME,
                        date = "01/06/2026"
                    ),
                    Transaction(
                        id = 2,
                        title = "Ăn trưa",
                        amount = 70_000.0,
                        category = "Ăn uống",
                        type = TransactionType.EXPENSE,
                        date = "02/06/2026"
                    ),
                    Transaction(
                        id = 3,
                        title = "Cà phê",
                        amount = 45_000.0,
                        category = "Giải trí",
                        type = TransactionType.EXPENSE,
                        date = "03/06/2026"
                    )
                ),
                totalTransactions = 3,
                totalIncome = 8_000_000.0,
                totalExpense = 115_000.0,
                balance = 7_885_000.0,
                averageAmount = 2_705_000.0,
                maxTransaction = Transaction(
                    id = 1,
                    title = "Lương tháng",
                    amount = 8_000_000.0,
                    category = "Lương",
                    type = TransactionType.INCOME,
                    date = "01/06/2026"
                ),
                minTransaction = Transaction(
                    id = 3,
                    title = "Cà phê",
                    amount = 45_000.0,
                    category = "Giải trí",
                    type = TransactionType.EXPENSE,
                    date = "03/06/2026"
                ),
                expenseByCategory = mapOf(
                    "Ăn uống" to 70_000.0,
                    "Giải trí" to 45_000.0
                ),
                categoryPercentages = mapOf(
                    "Ăn uống" to 60.87,
                    "Giải trí" to 39.13
                ),
                expenseTrend = listOf(
                    LineChartPoint("02/06/2026", 70_000.0),
                    LineChartPoint("03/06/2026", 45_000.0)
                )
            )
        )
    }
}
