/**
 * File: Theme.kt
 *
 * What it does:
 * Defines the Jetpack Compose Material Design 3 color schemes (light and dark) and root LabroxyTheme
 * composable wrapper for consistent application-wide styling.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.LabroxyApp: Wraps root UI components in LabroxyTheme.
 * - com.example.labroxy.data.AppSettings / SessionStore: Evaluates themeMode setting ("system", "light", "dark").
 *
 * Features / Functions:
 * - Defining light color scheme (LabroxyLightColors) with brand accent colors.
 * - Defining dark color scheme (LabroxyDarkColors) optimized for dark mode.
 * - Composable theme function (LabroxyTheme) that dynamically selects color palettes based on system theme or user setting.
 */
package com.example.labroxy.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LabroxyLightColors = lightColorScheme(
    primary = Color(0xFFFC6D26),
    onPrimary = Color.White,
    secondary = Color(0xFF2F6F73),
    onSecondary = Color.White,
    tertiary = Color(0xFF705C9C),
    background = Color(0xFFFAF9F6),
    onBackground = Color(0xFF22201F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF22201F),
    surfaceVariant = Color(0xFFEAE5DD),
    onSurfaceVariant = Color(0xFF5D5751),
    outline = Color(0xFF81766E),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val LabroxyDarkColors = darkColorScheme(
    primary = Color(0xFFFF9B63),
    onPrimary = Color(0xFF4C1D00),
    secondary = Color(0xFF80D0CF),
    onSecondary = Color(0xFF003738),
    tertiary = Color(0xFFD2BCFF),
    background = Color(0xFF171719),
    onBackground = Color(0xFFE9E3E0),
    surface = Color(0xFF202124),
    onSurface = Color(0xFFE9E3E0),
    surfaceVariant = Color(0xFF46464D),
    onSurfaceVariant = Color(0xFFC8C4CC),
    outline = Color(0xFF938F99),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun LabroxyTheme(
    themeMode: String = "system",
    forceMaterialColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        forceMaterialColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        dark -> LabroxyDarkColors
        else -> LabroxyLightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
