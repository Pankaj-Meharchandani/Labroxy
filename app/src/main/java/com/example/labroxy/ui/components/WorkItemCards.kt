package com.example.labroxy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.GitLabCommit
import com.example.labroxy.data.GitLabEvent
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabMergeRequest
import com.example.labroxy.data.GitLabProject
import com.example.labroxy.data.GitLabUser
import com.example.labroxy.ui.util.compactGitLabDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectCard(project: GitLabProject, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProjectAvatar(project.name)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(project.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(project.pathWithNamespace, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            project.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricChip(Icons.Outlined.Star, "${project.starCount}")
                MetricChip(Icons.Outlined.AccountTree, "${project.forksCount}")
                MetricChip(Icons.Outlined.TaskAlt, "${project.openIssuesCount} open")
                project.defaultBranch?.let { MetricChip(Icons.Outlined.Tag, it) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectHero(project: GitLabProject, branchCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                project.pathWithNamespace,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                project.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            project.description?.takeIf { it.isNotBlank() }?.let { Text(it) }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(Icons.Outlined.TaskAlt, "${project.openIssuesCount} issues")
                MetricChip(Icons.AutoMirrored.Outlined.MergeType, "$branchCount branches")
                MetricChip(Icons.Outlined.Star, "${project.starCount} stars")
            }
        }
    }
}

@Composable
fun IssueRow(issue: GitLabIssue, onUserClick: (String) -> Unit = {}, onClick: () -> Unit = {}) {
    ListCard(
        icon = Icons.Outlined.TaskAlt,
        user = issue.author,
        title = "#${issue.iid} ${issue.title}",
        meta = "${issue.state} by ${issue.author?.username ?: "unknown"}",
        onUserClick = onUserClick,
        onClick = onClick
    ) {
        LabelRow(issue.labels)
    }
}

@Composable
fun MergeRequestRow(mr: GitLabMergeRequest, onUserClick: (String) -> Unit = {}, onClick: () -> Unit = {}) {
    ListCard(
        icon = Icons.AutoMirrored.Outlined.MergeType,
        user = mr.author,
        title = "!${mr.iid} ${mr.title}",
        meta = "${mr.sourceBranch} into ${mr.targetBranch}",
        onUserClick = onUserClick,
        onClick = onClick
    ) {
        Text(mr.mergeStatus ?: mr.state, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CommitRow(commit: GitLabCommit) {
    ListCard(
        icon = Icons.Outlined.History,
        title = commit.title,
        meta = "${commit.shortId} by ${commit.authorName}"
    )
}

@Composable
fun ListCard(
    icon: ImageVector,
    title: String,
    meta: String,
    user: GitLabUser? = null,
    onClick: (() -> Unit)? = null,
    onUserClick: ((String) -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val modifier = if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            if (user != null) {
                UserAvatar(user = user, size = 36, onClick = { onUserClick?.invoke(user.username) })
            } else {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(enabled = onUserClick != null) {
                        user?.username?.let { onUserClick?.invoke(it) }
                    }
                )
                Text(meta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                content()
            }
        }
    }
}

@Composable
fun ActivityCard(event: GitLabEvent, onClick: () -> Unit) {
    val isClickable = event.projectId != null && event.targetIid != null &&
            event.targetType in listOf("Issue", "WorkItem", "MergeRequest")
    val title = event.targetTitle?.takeIf { it.isNotBlank() }
        ?: "${event.displayAction.replaceFirstChar { it.uppercase() }} ${event.targetType.orEmpty()}"
    val meta = "${event.displayAction} ${event.targetType.orEmpty()} ${compactGitLabDate(event.createdAt)}".trim()
    val icon = when (event.targetType) {
        "Issue", "WorkItem" -> Icons.AutoMirrored.Outlined.Assignment
        "MergeRequest" -> Icons.AutoMirrored.Outlined.MergeType
        else -> Icons.Outlined.History
    }

    ListCard(
        icon = icon,
        user = event.author,
        title = title,
        meta = meta,
        onClick = if (isClickable) onClick else null
    )
}

@Composable
fun UserHeroCard(user: GitLabUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatar(user = user, size = 80)
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
