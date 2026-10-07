package com.example.labroxy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.GitLabBoard
import com.example.labroxy.data.GitLabGroup
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabMergeRequest
import com.example.labroxy.data.GitLabProject
import com.example.labroxy.data.GitLabTodo
import com.example.labroxy.ui.DashboardData
import com.example.labroxy.ui.util.filteredIssues

fun LazyListScope.projectItems(
    projects: List<GitLabProject>,
    onProjectClick: (GitLabProject) -> Unit
) {
    if (projects.isEmpty()) {
        item { EmptyBlock("No projects matched that search.") }
    } else {
        items(projects, key = { it.id }) { project ->
            ProjectCard(project, onClick = { onProjectClick(project) })
        }
    }
}

fun LazyListScope.groupItems(groups: List<GitLabGroup>, onClick: (GitLabGroup) -> Unit) {
    if (groups.isEmpty()) {
        item { EmptyBlock("No groups matched that search.") }
    } else {
        items(groups, key = { it.id }) { group ->
            ListCard(
                icon = Icons.Outlined.AccountTree,
                title = group.name,
                meta = "${group.fullPath}${group.visibility?.let { " - $it" } ?: ""}",
                onClick = { onClick(group) }
            ) {
                group.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

fun LazyListScope.issueItems(issues: List<GitLabIssue>, empty: String, onClick: (GitLabIssue) -> Unit) {
    if (issues.isEmpty()) {
        item { EmptyBlock(empty) }
    } else {
        items(issues, key = { it.id }) { IssueRow(it, onClick = { onClick(it) }) }
    }
}

fun LazyListScope.assignedItems(
    data: DashboardData,
    query: String,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    onClick: (GitLabIssue) -> Unit
) {
    item {
        val openCount = data.assignedWorkItems.size
        val completedCount = data.assignedCompletedWorkItems.size

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { onTabChange(0) },
                label = { Text("Open $openCount") }
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { onTabChange(1) },
                label = { Text("Completed $completedCount") }
            )
        }
    }

    val issues = if (selectedTab == 0) {
        data.assignedWorkItems.filteredIssues(query)
    } else {
        data.assignedCompletedWorkItems.filteredIssues(query)
    }

    if (issues.isEmpty()) {
        item { EmptyBlock(if (selectedTab == 0) "Nothing is assigned to you." else "No completed assigned issues yet.") }
    } else {
        items(issues, key = { it.id }) { issue ->
            IssueRow(issue, onClick = { onClick(issue) })
        }
    }
}

fun LazyListScope.mrItems(mrs: List<GitLabMergeRequest>, onClick: (GitLabMergeRequest) -> Unit = {}) {
    if (mrs.isEmpty()) {
        item { EmptyBlock("No merge requests are assigned to you.") }
    } else {
        items(mrs, key = { it.id }) { MergeRequestRow(it, onClick = { onClick(it) }) }
    }
}

fun LazyListScope.todoItems(todos: List<GitLabTodo>, onClick: (GitLabTodo) -> Unit) {
    if (todos.isEmpty()) {
        item { EmptyBlock("Your to-do list is clear.") }
    } else {
        items(todos, key = { it.id }) { todo ->
            ListCard(
                icon = Icons.Outlined.TaskAlt,
                user = todo.author,
                title = todo.target?.title ?: todo.body ?: todo.targetType,
                meta = "${todo.action} - ${todo.targetType}${todo.project?.name?.let { " in $it" } ?: ""}",
                onClick = { onClick(todo) }
            )
        }
    }
}

fun LazyListScope.notificationItems(
    todos: List<GitLabTodo>,
    onTodoClick: (GitLabTodo) -> Unit
) {
    val items = todos.distinctBy { it.id }.sortedByDescending { it.createdAt }

    if (items.isEmpty()) {
        item { EmptyBlock("No pending GitLab to-dos.") }
    } else {
        items(items, key = { it.id }) { todo ->
            ListCard(
                icon = Icons.Outlined.NotificationsActive,
                user = todo.author,
                title = todo.target?.title ?: todo.body ?: todo.targetType,
                meta = "${todo.action} - ${todo.targetType}${todo.project?.name?.let { " in $it" } ?: ""}",
                onClick = { onTodoClick(todo) }
            )
        }
    }
}

fun LazyListScope.boardItems(boards: List<GitLabBoard>) {
    if (boards.isEmpty()) {
        item { EmptyBlock("This project has no issue boards.") }
    } else {
        items(boards, key = { it.id }) { board ->
            ListCard(
                icon = Icons.Outlined.AccountTree,
                title = board.name ?: "Issue board ${board.id}",
                meta = "Backlog ${if (board.hideBacklogList) "hidden" else "visible"} - Closed ${if (board.hideClosedList) "hidden" else "visible"}"
            )
        }
    }
}
