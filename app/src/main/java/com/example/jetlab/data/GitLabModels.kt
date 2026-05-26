package com.example.jetlab.data

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
    val id: Long,
    val name: String,
    @SerialName("path_with_namespace") val pathWithNamespace: String,
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
    val id: Long,
    val iid: Long,
    val title: String,
    val state: String,
    val labels: List<String> = emptyList(),
    @SerialName("web_url") val webUrl: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val author: GitLabUser? = null
)

@Serializable
data class GitLabTodoTarget(
    val id: Long? = null,
    val iid: Long? = null,
    val title: String? = null,
    @SerialName("web_url") val webUrl: String? = null
)

@Serializable
data class GitLabTodo(
    val id: Long,
    val action: String,
    val state: String,
    @SerialName("target_type") val targetType: String,
    @SerialName("body") val body: String? = null,
    val project: GitLabProject? = null,
    val target: GitLabTodoTarget? = null,
    val author: GitLabUser? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class GitLabMergeRequest(
    val id: Long,
    val iid: Long,
    val title: String,
    val state: String,
    @SerialName("source_branch") val sourceBranch: String,
    @SerialName("target_branch") val targetBranch: String,
    @SerialName("web_url") val webUrl: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("merge_status") val mergeStatus: String? = null,
    val author: GitLabUser? = null
)

@Serializable
data class GitLabEvent(
    val id: Long,
    val actionName: String? = null,
    @SerialName("action_name") val actionNameSnake: String? = null,
    @SerialName("target_type") val targetType: String? = null,
    @SerialName("target_title") val targetTitle: String? = null,
    @SerialName("project_id") val projectId: Long? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val author: GitLabUser? = null
) {
    val displayAction: String get() = actionName ?: actionNameSnake ?: "updated"
}

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
