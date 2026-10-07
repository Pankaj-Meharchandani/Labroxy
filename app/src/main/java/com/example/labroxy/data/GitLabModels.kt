/**
 * File: GitLabModels.kt
 *
 * What it does:
 * Defines Kotlin kotlinx.serialization data transfer objects (DTOs) representing GitLab REST API entities
 * and response models used throughout the data, domain, offline caching, and UI layers.
 *
 * Touchpoints:
 * - com.example.labroxy.data.GitLabApi: Used as return types and body payloads for Ktor network API calls.
 * - com.example.labroxy.data.GitLabRepository: Transport models across repository operations.
 * - com.example.labroxy.data.SessionStore: Serialized/deserialized for local DataStore JSON offline caching.
 * - com.example.labroxy.ui.LabroxyViewModel & com.example.labroxy.ui.LabroxyApp: State and UI rendering entities.
 *
 * Features / Functions:
 * - User models (GitLabUser).
 * - Project and group structure models (GitLabProject, GitLabNamespace, GitLabGroup).
 * - Issues, work items, labels, links, and widget relations (GitLabIssue, GitLabIssueReferences, GitLabLabel, GitLabIssueLink, GitLabWorkItemDetail, GitLabWorkItemWidget, GitLabRelatedItem).
 * - To-do item models (GitLabTodo, GitLabTodoTarget).
 * - Merge request models (GitLabMergeRequest).
 * - Activity and event models (GitLabEvent).
 * - Discussions, notes, and emoji award models (GitLabDiscussion, GitLabNote, GitLabAwardEmoji).
 * - Repository metadata models (GitLabCommit, GitLabBranch, GitLabBoard, GitLabUpload).
 */
package com.example.labroxy.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitLabUser(
    val id: Long,
    val username: String,
    val name: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

@Serializable
data class GitLabNamespace(
    val name: String = "",
    val path: String = "",
    @SerialName("full_path") val fullPath: String = path
)

@Serializable
data class GitLabProject(
    val id: Long = 0,
    val name: String = "",
    @SerialName("path_with_namespace") val pathWithNamespace: String = name,
    val description: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("web_url") val webUrl: String? = null,
    @SerialName("star_count") val starCount: Int = 0,
    @SerialName("forks_count") val forksCount: Int = 0,
    @SerialName("open_issues_count") val openIssuesCount: Int = 0,
    @SerialName("last_activity_at") val lastActivityAt: String? = null,
    @SerialName("default_branch") val defaultBranch: String? = null,
    val namespace: GitLabNamespace? = null
)

@Serializable
data class GitLabGroup(
    val id: Long,
    val name: String,
    val path: String,
    @SerialName("full_path") val fullPath: String,
    val description: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("web_url") val webUrl: String? = null,
    val visibility: String? = null
)

@Serializable
data class GitLabIssue(
    val id: Long = 0,
    val iid: Long = 0,
    val title: String = "",
    val description: String? = null,
    val state: String = "",
    @SerialName("project_id") val projectId: Long? = null,
    val labels: List<String> = emptyList(),
    @SerialName("web_url") val webUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val author: GitLabUser? = null,
    val assignee: GitLabUser? = null,
    val assignees: List<GitLabUser> = emptyList(),
    @SerialName("award_emoji") val awardEmoji: List<GitLabAwardEmoji> = emptyList(),
    @SerialName("references") val references: GitLabIssueReferences? = null
)

@Serializable
data class GitLabIssueReferences(
    val short: String? = null,
    val relative: String? = null,
    val full: String? = null
)

@Serializable
data class GitLabLabel(
    val id: Long = 0,
    val name: String = "",
    val color: String = "#6699cc",
    @SerialName("text_color") val textColor: String? = null,
    val description: String? = null
)

@Serializable
data class GitLabRelatedItem(
    val id: Long = 0,
    val iid: Long = 0,
    val title: String = "",
    val state: String = "",
    @SerialName("project_id") val projectId: Long? = null,
    @SerialName("web_url") val webUrl: String? = null,
    @SerialName("reference") val reference: String? = null
)

@Serializable
data class GitLabIssueLink(
    val id: Long = 0,
    val iid: Long = 0,
    val title: String = "",
    val state: String = "",
    @SerialName("project_id") val projectId: Long? = null,
    @SerialName("web_url") val webUrl: String? = null,
    @SerialName("issue_link_type") val issueLinkType: String? = null,
    @SerialName("link_type") val linkType: String? = null,
    @SerialName("source_issue") val sourceIssue: GitLabIssue? = null,
    @SerialName("target_issue") val targetIssue: GitLabIssue? = null,
    @SerialName("references") val references: GitLabIssueReferences? = null
)

@Serializable
data class GitLabWorkItemDetail(
    val id: Long = 0,
    val iid: Long = 0,
    val title: String = "",
    val state: String = "",
    @SerialName("project_id") val projectId: Long? = null,
    @SerialName("web_url") val webUrl: String? = null,
    val widgets: List<GitLabWorkItemWidget> = emptyList()
)

@Serializable
data class GitLabWorkItemWidget(
    val type: String = "",
    val parent: GitLabRelatedItem? = null,
    val children: GitLabRelatedItemNodes? = null,
    @SerialName("linked_items") val linkedItems: GitLabRelatedItemNodes? = null
)

@Serializable
data class GitLabRelatedItemNodes(
    val nodes: List<GitLabRelatedItem> = emptyList()
)

@Serializable
data class GitLabTodoTarget(
    val id: Long? = null,
    val iid: Long? = null,
    val title: String? = null,
    val state: String? = null,
    @SerialName("web_url") val webUrl: String? = null
)

@Serializable
data class GitLabTodo(
    val id: Long = 0,
    @SerialName("action_name") val action: String = "updated",
    val state: String = "",
    @SerialName("target_type") val targetType: String = "To-Do",
    @SerialName("body") val body: String? = null,
    val project: GitLabProject? = null,
    val target: GitLabTodoTarget? = null,
    val author: GitLabUser? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("target_url") val targetUrl: String? = null
)

@Serializable
data class GitLabMergeRequest(
    val id: Long = 0,
    val iid: Long = 0,
    val title: String = "",
    val description: String? = null,
    val state: String = "",
    @SerialName("project_id") val projectId: Long? = null,
    @SerialName("source_branch") val sourceBranch: String,
    @SerialName("target_branch") val targetBranch: String,
    @SerialName("web_url") val webUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("merge_status") val mergeStatus: String? = null,
    val author: GitLabUser? = null,
    @SerialName("award_emoji") val awardEmoji: List<GitLabAwardEmoji> = emptyList()
)

@Serializable
data class GitLabEvent(
    val id: Long,
    val actionName: String? = null,
    @SerialName("action_name") val actionNameSnake: String? = null,
    @SerialName("target_type") val targetType: String? = null,
    @SerialName("target_iid") val targetIid: Long? = null,
    @SerialName("target_id") val targetId: Long? = null,
    @SerialName("target_title") val targetTitle: String? = null,
    @SerialName("project_id") val projectId: Long? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val author: GitLabUser? = null
) {
    val displayAction: String get() = actionName ?: actionNameSnake ?: "updated"
}

@Serializable
data class GitLabAwardEmoji(
    val id: Long = 0,
    val name: String = "",
    val user: GitLabUser = GitLabUser(id = 0, username = "gitlab", name = "GitLab"),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("awardable_id") val awardableId: Long = 0,
    @SerialName("awardable_type") val awardableType: String = ""
)

@Serializable
data class GitLabNote(
    val id: Long,
    val body: String,
    val type: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val author: GitLabUser? = null,
    val system: Boolean = false,
    val resolvable: Boolean = false,
    val resolved: Boolean? = null,
    @SerialName("award_emoji") val awardEmoji: List<GitLabAwardEmoji> = emptyList()
)

@Serializable
data class GitLabDiscussion(
    val id: String,
    @SerialName("individual_note") val individualNote: Boolean = false,
    val notes: List<GitLabNote> = emptyList()
)

@Serializable
data class GitLabBoard(
    val id: Long,
    val name: String? = null,
    @SerialName("hide_backlog_list") val hideBacklogList: Boolean = false,
    @SerialName("hide_closed_list") val hideClosedList: Boolean = false
)

@Serializable
data class GitLabCommit(
    val id: String,
    @SerialName("short_id") val shortId: String,
    val title: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("committed_date") val committedDate: String? = null,
    @SerialName("web_url") val webUrl: String? = null
)

@Serializable
data class GitLabBranch(
    val name: String,
    val merged: Boolean = false,
    @SerialName("protected") val isProtected: Boolean = false,
    @SerialName("default") val isDefault: Boolean = false
)

@Serializable
data class GitLabUpload(
    val alt: String,
    val url: String,
    val fullPath: String? = null,
    val markdown: String
)
