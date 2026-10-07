package com.example.labroxy.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.labroxy.data.AppSettings
import com.example.labroxy.ui.DashboardData
import com.example.labroxy.ui.navigation.WorkSection
import com.example.labroxy.ui.util.formatRelativeTime

data class DashboardCardSpec(
    val id: String,
    val title: String,
    val count: Int,
    val subtitle: String,
    val timestamp: String,
    val icon: ImageVector,
    val section: WorkSection,
    val isEnabled: Boolean,
    val gradient: List<Color>
)

fun buildDashboardCardSpecs(data: DashboardData, settings: AppSettings): List<DashboardCardSpec> {
    val mrTime = formatRelativeTime(data.assignedMergeRequests.mapNotNull { it.updatedAt ?: it.createdAt }.maxOrNull())
    val todoTime = formatRelativeTime(data.todos.mapNotNull { it.createdAt }.maxOrNull())
    val issueTime = formatRelativeTime(data.assignedWorkItems.mapNotNull { it.updatedAt ?: it.createdAt }.maxOrNull())
    val projectTime = formatRelativeTime(data.projects.mapNotNull { it.lastActivityAt }.maxOrNull())
    val groupTime = formatRelativeTime(data.events.mapNotNull { it.createdAt }.maxOrNull())

    val allSpecs = listOf(
        DashboardCardSpec(
            id = "mrs",
            title = "Merge requests",
            count = data.assignedMergeRequests.size,
            subtitle = "Assigned to you",
            timestamp = mrTime,
            icon = Icons.AutoMirrored.Outlined.MergeType,
            section = WorkSection.MergeRequests,
            isEnabled = settings.showMergeRequestsTab,
            gradient = listOf(Color(0xFFFF5722), Color(0xFFF4511E))
        ),
        DashboardCardSpec(
            id = "todos",
            title = "To-Do List",
            count = data.todos.size,
            subtitle = "Need your attention",
            timestamp = todoTime,
            icon = Icons.Outlined.TaskAlt,
            section = WorkSection.Todos,
            isEnabled = settings.showTodosTab,
            gradient = listOf(Color(0xFF9C27B0), Color(0xFF8E24AA))
        ),
        DashboardCardSpec(
            id = "assigned",
            title = "Work items",
            count = data.assignedWorkItems.size,
            subtitle = "Assigned to you",
            timestamp = issueTime,
            icon = Icons.AutoMirrored.Outlined.Assignment,
            section = WorkSection.Assigned,
            isEnabled = settings.showAssignedTab,
            gradient = listOf(Color(0xFF009688), Color(0xFF00897B))
        ),
        DashboardCardSpec(
            id = "projects",
            title = "Projects",
            count = data.projects.size,
            subtitle = "Accessible",
            timestamp = projectTime,
            icon = Icons.Outlined.Folder,
            section = WorkSection.Projects,
            isEnabled = settings.showProjectsTab,
            gradient = listOf(Color(0xFF2196F3), Color(0xFF1E88E5))
        ),
        DashboardCardSpec(
            id = "groups",
            title = "Groups",
            count = data.groups.size,
            subtitle = "Member groups",
            timestamp = groupTime,
            icon = Icons.Outlined.AccountTree,
            section = WorkSection.Groups,
            isEnabled = settings.showGroupsTab,
            gradient = listOf(Color(0xFF3F51B5), Color(0xFF3949AB))
        )
    )

    return allSpecs.filter { it.isEnabled }
}
