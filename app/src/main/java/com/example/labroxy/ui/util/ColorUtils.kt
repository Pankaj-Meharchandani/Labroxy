package com.example.labroxy.ui.util

import androidx.compose.ui.graphics.Color

fun gitLabColor(value: String?, fallback: Color): Color {
    val clean = value?.trim()?.removePrefix("#") ?: return fallback
    if (clean.length != 6) return fallback
    val parsed = clean.toLongOrNull(16) ?: return fallback
    return Color((0xFF000000L or parsed).toInt())
}

fun readableOn(background: Color): Color {
    val luminance = (background.red * 0.299f) + (background.green * 0.587f) + (background.blue * 0.114f)
    return if (luminance > 0.58f) Color(0xFF1F1F24) else Color.White
}
