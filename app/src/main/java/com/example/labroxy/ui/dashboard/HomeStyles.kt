package com.example.labroxy.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Your work", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Welcome, ${data.user.name}. Browse all accessible projects, groups, assigned work, reviews, to-dos, and recent activity.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
    item { WelcomeBlock(data) }
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserAvatar(user = data.user, size = 68)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = data.user.name.ifBlank { data.user.username },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Welcome!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
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
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
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
                            text = "Items that need your attention",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(
                            onClick = { onSectionChange(WorkSection.Todos) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Everything",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
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
                        Text(
                            text = "No items requiring attention.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            attentionItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = item.onClick)
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.InsertDriveFile,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                    )
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "$count",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
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

    item {
        val colorScheme = MaterialTheme.colorScheme
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.85f),
                                colorScheme.tertiary.copy(alpha = 0.75f),
                                colorScheme.secondary.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        UserAvatar(user = data.user, size = 64)
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Hello, ${data.user.name.ifBlank { data.user.username }} 👋",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "@${data.user.username} · GitLab Cockpit",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.82f)
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cards.take(3).forEach { card ->
                            StylishHeroPill(
                                icon = card.icon,
                                label = "${card.count} ${card.title}",
                                onClick = { onSectionChange(card.section) }
                            )
                        }
                    }
                }
            }
        }
    }

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
                    Text("View All", fontWeight = FontWeight.Bold)
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

@Composable
fun StylishHeroPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.22f),
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
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
