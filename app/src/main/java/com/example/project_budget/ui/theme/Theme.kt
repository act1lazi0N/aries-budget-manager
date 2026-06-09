package com.example.project_budget.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AriesLightOrange,
    onPrimary = AriesText,
    primaryContainer = AriesOrange,
    onPrimaryContainer = AriesDarkText,
    secondary = AriesLightRed,
    onSecondary = AriesText,
    secondaryContainer = AriesRed,
    onSecondaryContainer = AriesDarkText,
    tertiary = AriesLightYellow,
    onTertiary = AriesText,
    background = AriesDarkBackground,
    onBackground = AriesDarkText,
    surface = AriesDarkSurface,
    onSurface = AriesDarkText,
    error = AriesLightRed,
    onError = AriesText
)

private val LightColorScheme = lightColorScheme(
    primary = AriesOrange,
    onPrimary = AriesSurface,
    primaryContainer = AriesLightOrange,
    onPrimaryContainer = AriesText,
    secondary = AriesRed,
    onSecondary = AriesSurface,
    secondaryContainer = AriesLightRed,
    onSecondaryContainer = AriesText,
    tertiary = AriesYellow,
    onTertiary = AriesText,
    tertiaryContainer = AriesLightYellow,
    onTertiaryContainer = AriesText,
    background = AriesCream,
    onBackground = AriesText,
    surface = AriesSurface,
    onSurface = AriesText,
    surfaceVariant = AriesLightOrange,
    onSurfaceVariant = AriesMutedText,
    error = AriesError,
    onError = AriesSurface
)
@Composable
fun Project_BudgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
