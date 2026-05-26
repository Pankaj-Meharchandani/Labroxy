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
    @SerialName("web_url") val webUrl: String,
    @SerialName("star_count") val starCount: Int = 0,
    @SerialName("forks_count") val forksCount: Int = 0,
    @SerialName("open_issues_count") val openIssuesCount: Int = 0,
    @SerialName("last_activity_at") val lastActivityAt: String? = null,
    @SerialName("default_branch") val defaultBranch: String? = null,
    val namespace: GitLabNamespace? = null
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
