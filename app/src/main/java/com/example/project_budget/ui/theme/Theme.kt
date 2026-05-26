package com.example.project_budget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val LightColorScheme = lightColorScheme(

    primary = OrangePrimary,
    primaryContainer = OrangeLight,
    secondary = RedPrimary,
    tertiary = YellowPrimary,
    background = CreamBackground,
    surface = CardWhite,
    onPrimary = CardWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    onSecondary = GreenPrimary,

)
@Composable
fun Project_BudgetTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}