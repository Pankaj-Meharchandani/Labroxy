/**
 * File: DetailSections.kt
 *
 * What it does:
 * Renders assignee people sections and related work item hierarchy/linked item sections in work detail views.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.screens.detail.WorkDetailScreen: Hosted in work detail summary card.
 * - com.example.labroxy.ui.components.Avatars: Renders `UserAvatar` chips in assignee section.
 *
 * Features / Functions:
 * - `DetailPeopleSection`: Flow row of user avatar surface chips displaying assigned users with user click callbacks.
 * - `DetailRelationSection`: Vertical list of surface cards displaying parent, child, or linked work items with navigation links.
 */
package com.example.labroxy.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.GitLabRelatedItem
import com.example.labroxy.data.GitLabUser
import com.example.labroxy.ui.components.UserAvatar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailPeopleSection(
    title: String,
    users: List<GitLabUser>,
    onUserClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            users.forEach { user ->
                Surface(
                    onClick = { onUserClick(user.username) },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserAvatar(user = user, size = 26)
                        Text(
                            user.name.ifBlank { "@${user.username}" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.widthIn(max = 180.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRelationSection(
    title: String,
    items: List<GitLabRelatedItem>,
    onGitLabLinkClick: (String) -> Boolean
) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items.take(5).forEach { item ->
                Surface(
                    onClick = { item.webUrl?.let { onGitLabLinkClick(it) } },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AccountTree,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                item.title.ifBlank { item.reference ?: "Related item" },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            val meta = listOfNotNull(
                                item.reference,
                                item.state.takeIf { it.isNotBlank() }
                            ).distinct().joinToString(" · ")
                            if (meta.isNotBlank()) {
                                Text(
                                    meta,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            if (items.size > 5) {
                Text(
                    "+ ${items.size - 5} more",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
