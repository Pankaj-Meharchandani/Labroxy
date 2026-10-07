/**
 * File: HomeStyles.kt
 *
 * What it does:
 * Renders home screen feed variants (`Minimal`, `Functional`, `Stylish`), welcome header blocks, and recent activity sections.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.dashboard.DashboardScreen: Invokes `homeItems` inside DashboardScreen LazyColumn.
 * - com.example.labroxy.data.AppSettings: Evaluates `homeUiStyle` setting ("minimal", "functional", "stylish").
 * - com.example.labroxy.ui.components.WorkItemCards: Renders `ActivityCard` items.
 *
 * Features / Functions:
 * - `WelcomeBlock`: User welcome text banner.
 * - `homeItems`: Router function selecting home layout implementation based on user setting.
 * - `homeItemsMinimal`: Clean list layout with simple navigation rows.
 * - `homeItemsFunctional`: Grid layout with header card, metric cards, and attention items.
 * - `homeItemsStylish`: Expressive gradient hero banner, pill stats, horizontal metric carousel, and focus workspace.
 * - `RecentActivitiesSection`: List of 5 most recent user and project events.
 */
package com.example.labroxy.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labroxy.data.AppSettings
import com.example.labroxy.data.GitLabEvent
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabTodo
import com.example.labroxy.ui.DashboardData
import com.example.labroxy.ui.components.ActivityCard
import com.example.labroxy.ui.components.UserAvatar
import com.example.labroxy.ui.navigation.HomeNavRow
import com.example.labroxy.ui.navigation.WorkSection
import com.example.labroxy.ui.util.formatRelativeTime

@Composable
fun WelcomeBlock(data: DashboardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserAvatar(user = data.user, size = 56)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = data.user.name.ifBlank { data.user.username },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${data.user.username} · GitLab Workspace",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun LazyListScope.homeItems(
    data: DashboardData,
    settings: AppSettings,
    onSectionChange: (WorkSection) -> Unit,
    onEventClick: (GitLabEvent) -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onTodoClick: (GitLabTodo) -> Unit
) {
    when (settings.homeUiStyle) {
        "functional" -> homeItemsFunctional(
            data = data,
            settings = settings,
            onSectionChange = onSectionChange,
            onEventClick = onEventClick,
            onIssueClick = onIssueClick,
            onTodoClick = onTodoClick
        )
        "stylish" -> homeItemsStylish(
            data = data,
            settings = settings,
            onSectionChange = onSectionChange,
            onEventClick = onEventClick,
            onIssueClick = onIssueClick,
            onTodoClick = onTodoClick
        )
        else -> homeItemsMinimal(
            data = data,
            settings = settings,
            onSectionChange = onSectionChange,
            onEventClick = onEventClick
        )
    }
}

fun LazyListScope.homeItemsMinimal(
    data: DashboardData,
    settings: AppSettings,
    onSectionChange: (WorkSection) -> Unit,
    onEventClick: (GitLabEvent) -> Unit
) {
    item {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Hello, ${data.user.name.ifBlank { data.user.username }}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    item {
        Text(
            text = "WORKSPACE SECTIONS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
    }

    if (settings.showProjectsTab) {
        item { HomeNavRow(WorkSection.Projects, data.projects.size, onSectionChange) }
    }
    if (settings.showGroupsTab) {
        item { HomeNavRow(WorkSection.Groups, data.groups.size, onSectionChange) }
    }
    if (settings.showAssignedTab) {
        item { HomeNavRow(WorkSection.Assigned, data.assignedWorkItems.size, onSectionChange) }
    }
    if (settings.showMergeRequestsTab) {
        item { HomeNavRow(WorkSection.MergeRequests, data.assignedMergeRequests.size, onSectionChange) }
    }
    if (settings.showTodosTab) {
        item { HomeNavRow(WorkSection.Todos, data.todos.size, onSectionChange) }
    }
    if (settings.showActivities) {
        item {
            RecentActivitiesSection(
                data = data,
                onEventClick = onEventClick
            )
        }
    }
}

fun LazyListScope.homeItemsFunctional(
    data: DashboardData,
    settings: AppSettings,
    onSectionChange: (WorkSection) -> Unit,
    onEventClick: (GitLabEvent) -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onTodoClick: (GitLabTodo) -> Unit
) {
    item {
        WelcomeBlock(data)
    }

    item {
        val cards = buildDashboardCardSpecs(data, settings)
        if (cards.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                cards.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FunctionalDashboardCard(
                            title = pair[0].title,
                            count = pair[0].count,
                            subtitle = pair[0].subtitle,
                            timestamp = pair[0].timestamp,
                            icon = pair[0].icon,
                            accentColor = pair[0].gradient.firstOrNull() ?: MaterialTheme.colorScheme.primary,
                            onClick = { onSectionChange(pair[0].section) },
                            modifier = Modifier.weight(1f)
                        )
                        if (pair.size > 1) {
                            FunctionalDashboardCard(
                                title = pair[1].title,
                                count = pair[1].count,
                                subtitle = pair[1].subtitle,
                                timestamp = pair[1].timestamp,
                                icon = pair[1].icon,
                                accentColor = pair[1].gradient.firstOrNull() ?: MaterialTheme.colorScheme.primary,
                                onClick = { onSectionChange(pair[1].section) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    if (settings.showTodosTab) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTION REQUIRED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        TextButton(
                            onClick = { onSectionChange(WorkSection.Todos) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "View All (${data.todos.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    val attentionItems = remember(data.todos) {
                        data.todos.map { todo ->
                            FunctionalAttentionItem(
                                id = todo.id,
                                title = todo.target?.title ?: todo.body ?: todo.targetType,
                                subtitle = "${todo.author?.name ?: todo.author?.username ?: "Someone"} ${todo.action} - ${todo.project?.name.orEmpty()}".trim().removeSuffix("-").trim(),
                                timestamp = formatRelativeTime(todo.createdAt),
                                onClick = { onTodoClick(todo) }
                            )
                        }.take(5)
                    }

                    if (attentionItems.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "All clear! No pending items requiring attention.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            attentionItems.forEach { item ->
                                Surface(
                                    onClick = item.onClick,
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Outlined.InsertDriveFile,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (item.subtitle.isNotBlank()) {
                                                Text(
                                                    text = item.subtitle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            if (item.timestamp.isNotBlank()) {
                                                Text(
                                                    text = item.timestamp,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (settings.showActivities) {
        item {
            RecentActivitiesSection(
                data = data,
                onEventClick = onEventClick
            )
        }
    }
}

@Composable
fun FunctionalDashboardCard(
    title: String,
    count: Int,
    subtitle: String,
    timestamp: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (timestamp.isNotBlank()) {
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

data class FunctionalAttentionItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalLayoutApi::class)
fun LazyListScope.homeItemsStylish(
    data: DashboardData,
    settings: AppSettings,
    onSectionChange: (WorkSection) -> Unit,
    onEventClick: (GitLabEvent) -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onTodoClick: (GitLabTodo) -> Unit
) {
    val cards = buildDashboardCardSpecs(data, settings)

    // Handcrafted Workspace Header
    item {
        StylishWorkspaceHeader(
            data = data,
            cards = cards,
            onSectionChange = onSectionChange
        )
    }

    // Expressive Horizontal Quick Carousel
    item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "QUICK DASHBOARD",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    StylishMetricCard(
                        title = card.title,
                        count = card.count,
                        subtitle = card.subtitle,
                        icon = card.icon,
                        gradient = card.gradient,
                        onClick = { onSectionChange(card.section) }
                    )
                }
            }
        }
    }

    // Focus Workspace Feed
    item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FOCUS WORKSPACE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { onSectionChange(WorkSection.Todos) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("View All", fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            val topTodos = data.todos.take(4)
            if (topTodos.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No pending tasks in your focus workspace.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    topTodos.forEach { todo ->
                        StylishTaskCard(
                            todo = todo,
                            onClick = { onTodoClick(todo) }
                        )
                    }
                }
            }
        }
    }

    if (settings.showActivities) {
        item {
            RecentActivitiesSection(
                data = data,
                onEventClick = onEventClick
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StylishWorkspaceHeader(
    data: DashboardData,
    cards: List<DashboardCardSpec>,
    onSectionChange: (WorkSection) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    colorScheme.primary.copy(alpha = 0.75f),
                    colorScheme.tertiary.copy(alpha = 0.6f),
                    colorScheme.secondary.copy(alpha = 0.75f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.primaryContainer.copy(alpha = 0.35f),
                            colorScheme.surface
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(2.dp, colorScheme.primary, CircleShape)
                    ) {
                        UserAvatar(user = data.user, size = 58)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = data.user.name.ifBlank { data.user.username },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "@${data.user.username} · GitLab Cockpit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (cards.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        cards.take(3).forEach { card ->
                            val accentColor = card.gradient.firstOrNull() ?: colorScheme.primary
                            Surface(
                                onClick = { onSectionChange(card.section) },
                                shape = RoundedCornerShape(20.dp),
                                color = colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = card.icon,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "${card.count}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = colorScheme.onSurface
                                    )
                                    Text(
                                        text = card.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StylishMetricCard(
    title: String,
    count: Int,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.82f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun StylishTaskCard(
    todo: GitLabTodo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (todo.author != null) {
                UserAvatar(user = todo.author, size = 42)
            } else {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TaskAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = todo.target?.title ?: todo.body ?: todo.targetType,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(todo.action, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(24.dp)
                    )
                    Text(
                        text = todo.project?.name.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivitiesSection(
    data: DashboardData,
    onEventClick: (GitLabEvent) -> Unit
) {
    val recentActivities = remember(data.events, data.projectEvents, data.user) {
        val allEvents = (data.events + data.projectEvents).distinctBy { it.id }
        val userEvents = allEvents.filter { event ->
            event.author?.id == data.user.id ||
            (data.user.username.isNotBlank() && event.author?.username.equals(data.user.username, ignoreCase = true))
        }
        (if (userEvents.isNotEmpty()) userEvents else allEvents)
            .sortedByDescending { it.createdAt }
            .take(5)
    }

    if (recentActivities.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "RECENT ACTIVITIES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            recentActivities.forEach { event ->
                ActivityCard(event = event, onClick = { onEventClick(event) })
            }
        }
    }
}
