/**
 * File: UiState.kt
 *
 * What it does:
 * Defines generic sealed interface LoadState<T> representing asynchronous UI state (Loading, Success, Error)
 * along with utility extension functions for error message handling.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.LabroxyViewModel: Wraps dashboard, project, group, detail, and user state flows in LoadState.
 * - com.example.labroxy.ui.LabroxyApp: Evaluates LoadState to render progress indicators, error views, or content cards.
 *
 * Features / Functions:
 * - Sealed interface LoadState<T> (Loading, Success, Error).
 * - Exception to user-friendly message conversion (Throwable.toFriendlyMessage()).
 */
package com.example.labroxy.ui

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Success<T>(val value: T) : LoadState<T>
    data class Error(val message: String) : LoadState<Nothing>
}

fun Throwable.toFriendlyMessage(): String =
    message?.takeIf { it.isNotBlank() } ?: "Something went wrong. Check your GitLab host and token."
