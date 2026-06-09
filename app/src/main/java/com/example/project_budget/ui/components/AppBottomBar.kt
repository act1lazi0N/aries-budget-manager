package com.example.project_budget.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.project_budget.ui.navigation.Screen

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        Screen.Home,
        Screen.Transactions,
        Screen.Statistics,
        Screen.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen) },
                icon = {
                    BottomBarIcon(screen = screen)
                },
                label = { Text(text = screen.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun BottomBarIcon(screen: Screen) {
    when (screen) {
        Screen.Home -> MaterialBottomBarIcon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Trang chủ"
        )

        Screen.Transactions -> MaterialBottomBarIcon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = "Giao dịch"
        )

        Screen.Statistics -> StatisticsBottomBarIcon()

        Screen.Settings -> MaterialBottomBarIcon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Cài đặt"
        )

        else -> Text(text = screen.shortLabel)
    }
}

@Composable
private fun MaterialBottomBarIcon(
    imageVector: ImageVector,
    contentDescription: String
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun StatisticsBottomBarIcon() {
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(24.dp)) {
        drawChartBar(color = color, left = 0.2f, top = 0.52f, height = 0.32f)
        drawChartBar(color = color, left = 0.42f, top = 0.32f, height = 0.52f)
        drawChartBar(color = color, left = 0.64f, top = 0.16f, height = 0.68f)
    }
}

private fun DrawScope.drawChartBar(
    color: Color,
    left: Float,
    top: Float,
    height: Float
) {
    val scaledLeft = size.width * left
    val scaledTop = size.height * top
    val scaledWidth = size.width * 0.16f
    val scaledHeight = size.height * height
    drawRoundRect(
        color = color,
        topLeft = Offset(scaledLeft, scaledTop),
        size = Size(width = scaledWidth, height = scaledHeight),
        cornerRadius = CornerRadius(scaledWidth / 2f, scaledWidth / 2f)
    )
}
