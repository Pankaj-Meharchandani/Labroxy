/**
 * File: ColorUtils.kt
 *
 * What it does:
 * Provides utility functions for parsing hex color codes and determining high-contrast text color against dynamic backgrounds.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.components.ChipsAndBadges: Calculates background and text colors for `GitLabLabelChip`.
 * - com.example.labroxy.data.GitLabLabel: Uses color hex strings from GitLab label API payloads.
 *
 * Features / Functions:
 * - Hex string to Compose `Color` conversion (`gitLabColor`).
 * - Luminance-based high contrast foreground text color selection (`readableOn` - returns dark text or white text).
 */
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
