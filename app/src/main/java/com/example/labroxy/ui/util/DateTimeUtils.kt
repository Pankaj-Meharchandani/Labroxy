/**
 * File: DateTimeUtils.kt
 *
 * What it does:
 * Provides pure utility functions for parsing ISO 8601 timestamps and formatting human-readable relative time strings.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.components.WorkItemCards: Formats activity timestamps on activity and comment cards.
 * - com.example.labroxy.ui.dashboard.DashboardCardSpec: Calculates relative timestamps for dashboard metric specs.
 * - com.example.labroxy.ui.screens.detail.DiscussionComponents: Displays comment dates.
 *
 * Features / Functions:
 * - Relative time string formatting (`formatRelativeTime` e.g., "Just now", "5m ago", "2h ago", "3d ago").
 * - Parsing ISO timestamps to epoch milliseconds (`parseIsoTimeToEpochMillis`).
 * - Compact date formatting (`compactGitLabDate` e.g., "yyyy-MM-dd HH:mm").
 */
package com.example.labroxy.ui.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import java.util.TimeZone

fun formatRelativeTime(value: String?): String {
    if (value.isNullOrBlank()) return "Just now"
    val millis = parseIsoTimeToEpochMillis(value) ?: return compactGitLabDate(value).ifBlank { "Just now" }
    val diff = System.currentTimeMillis() - millis
    if (diff < 0) return "Just now"

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}hrs ago"
        days < 30 -> "${days}d ago"
        else -> "${days / 30}mo ago"
    }
}

fun parseIsoTimeToEpochMillis(value: String): Long? {
    return runCatching {
        Instant.parse(value).toEpochMilli()
    }.getOrNull() ?: runCatching {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        format.parse(value.substringBefore("."))?.time
    }.getOrNull()
}

fun compactGitLabDate(value: String?): String {
    if (value.isNullOrBlank()) return ""
    val date = value.substringBefore("T")
    val time = value.substringAfter("T", "").take(5)
    return listOf(date, time).filter { it.isNotBlank() }.joinToString(" ")
}
