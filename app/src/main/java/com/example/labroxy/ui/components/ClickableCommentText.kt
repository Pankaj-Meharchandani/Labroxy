/**
 * File: ClickableCommentText.kt
 *
 * What it does:
 * Renders annotated text with styled links (`http://`, `https://`, markdown links), `@` user mentions, bold, and italic text formatting with click handlers.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.screens.detail.DiscussionComponents: Renders comment bodies inside `NoteBody`.
 *
 * Features / Functions:
 * - Parsing URLs, markdown links, user handles (`@username`), bold (`**text**`), and italics (`*text*`).
 * - Highlighting links and mentions in primary theme accent colors.
 * - Triggering `onLinkClick` or `onUserClick` based on tapped text range annotation.
 */
package com.example.labroxy.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ClickableCommentText(
    text: String,
    onLinkClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pattern = Regex("""!?\[([^\]]+)]\((https?://[^)\s]+)(?:\s+"[^"]*")?\)|https?://[^\s)]+|@([A-Za-z0-9_.-]+)|\*\*([^*]+)\*\*|\*([^*]+)\*|__([^_]+)__| _([^_]+)_""")
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        pattern.findAll(text).forEach { result ->
            val matchRange = result.range
            if (matchRange.first > lastIndex) {
                append(text.substring(lastIndex, matchRange.first))
            }

            val start = length
            val markdownLabel = result.groups[1]?.value
            val markdownUrl = result.groups[2]?.value
            val username = result.groups[3]?.value
            val boldText = result.groups[4]?.value ?: result.groups[6]?.value
            val italicText = result.groups[5]?.value ?: result.groups[7]?.value
            val plainUrl = result.value.takeIf { it.startsWith("http://") || it.startsWith("https://") }

            when {
                markdownUrl != null -> {
                    val label = markdownLabel ?: markdownUrl
                    append(label)
                    addStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                        start, length
                    )
                    addStringAnnotation("LINK", markdownUrl, start, length)
                }
                plainUrl != null -> {
                    val url = plainUrl.trimEnd('.', ',', ')')
                    append(url)
                    addStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                        start, length
                    )
                    addStringAnnotation("LINK", url, start, length)
                }
                username != null -> {
                    append("@$username")
                    addStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                        start, length
                    )
                    addStringAnnotation("USER", username, start, length)
                }
                boldText != null -> {
                    append(boldText)
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, length)
                }
                italicText != null -> {
                    append(italicText)
                    addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, length)
                }
            }
            lastIndex = matchRange.last + 1
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
    
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "LINK", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onLinkClick(annotation.item)
                    return@ClickableText
                }
            annotatedString.getStringAnnotations(tag = "USER", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onUserClick(annotation.item)
                }
        }
    )
}
