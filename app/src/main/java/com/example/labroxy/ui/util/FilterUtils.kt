/**
 * File: FilterUtils.kt
 *
 * What it does:
 * Provides Kotlin extension functions on list collections of GitLab domain models to execute case-insensitive search filtering.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.dashboard.DashboardScreen: Filters projects, groups, issues, MRs, to-dos, and events based on query string.
 * - com.example.labroxy.data.GitLabModels: Operates on `List<GitLabProject>`, `List<GitLabGroup>`, `List<GitLabIssue>`, `List<GitLabMergeRequest>`, `List<GitLabTodo>`, and `List<GitLabEvent>`.
 *
 * Features / Functions:
 * - `filteredProjects`: Filters projects by name, path with namespace, or description.
 * - `filteredGroups`: Filters groups by name, full path, or description.
 * - `filteredIssues`: Filters issues by title, state, labels, or author.
 * - `filteredMergeRequests`: Filters merge requests by title, branches, state, or author.
 * - `filteredTodos`: Filters to-dos by target title, body, target type, or project name.
 * - `filteredEvents`: Filters activity events by target title, target type, action, or author.
 */
package com.example.labroxy.ui.util

import com.example.labroxy.data.GitLabEvent
import com.example.labroxy.data.GitLabGroup
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabMergeRequest
import com.example.labroxy.data.GitLabProject
import com.example.labroxy.data.GitLabTodo

fun List<GitLabProject>.filteredProjects(query: String): List<GitLabProject> =
    if (query.isBlank()) this else filter {
        it.name.contains(query, ignoreCase = true) ||
            it.pathWithNamespace.contains(query, ignoreCase = true) ||
            it.description.orEmpty().contains(query, ignoreCase = true)
    }

fun List<GitLabGroup>.filteredGroups(query: String): List<GitLabGroup> =
    if (query.isBlank()) this else filter {
        it.name.contains(query, ignoreCase = true) ||
            it.fullPath.contains(query, ignoreCase = true) ||
            it.description.orEmpty().contains(query, ignoreCase = true)
    }

fun List<GitLabIssue>.filteredIssues(query: String): List<GitLabIssue> =
    if (query.isBlank()) this else filter {
        it.title.contains(query, ignoreCase = true) ||
            it.state.contains(query, ignoreCase = true) ||
            it.labels.any { label -> label.contains(query, ignoreCase = true) } ||
            it.author?.username.orEmpty().contains(query, ignoreCase = true)
    }

fun List<GitLabMergeRequest>.filteredMergeRequests(query: String): List<GitLabMergeRequest> =
    if (query.isBlank()) this else filter {
        it.title.contains(query, ignoreCase = true) ||
            it.state.contains(query, ignoreCase = true) ||
            it.sourceBranch.contains(query, ignoreCase = true) ||
            it.targetBranch.contains(query, ignoreCase = true) ||
            it.author?.username.orEmpty().contains(query, ignoreCase = true)
    }

fun List<GitLabTodo>.filteredTodos(query: String): List<GitLabTodo> =
    if (query.isBlank()) this else filter {
        it.target?.title.orEmpty().contains(query, ignoreCase = true) ||
            it.body.orEmpty().contains(query, ignoreCase = true) ||
            it.targetType.contains(query, ignoreCase = true) ||
            it.project?.name.orEmpty().contains(query, ignoreCase = true)
    }

fun List<GitLabEvent>.filteredEvents(query: String): List<GitLabEvent> =
    if (query.isBlank()) this else filter {
        it.targetTitle.orEmpty().contains(query, ignoreCase = true) ||
            it.targetType.orEmpty().contains(query, ignoreCase = true) ||
            it.displayAction.contains(query, ignoreCase = true) ||
            it.author?.username.orEmpty().contains(query, ignoreCase = true)
    }
