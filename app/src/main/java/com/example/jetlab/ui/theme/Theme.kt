package com.example.jetlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JetLabColors = lightColorScheme(
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
    error = Color(0xFFBA1A1A)
)

@Composable
fun JetLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JetLabColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
