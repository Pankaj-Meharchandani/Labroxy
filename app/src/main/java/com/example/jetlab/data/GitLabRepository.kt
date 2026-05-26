package com.example.jetlab.data

class GitLabRepository(
    private val session: GitLabSession
) {
    private val api = GitLabApi(session.host) { session.token }

    suspend fun me(): GitLabUser = api.currentUser()
    suspend fun projects(query: String): List<GitLabProject> = api.projects(query)
    suspend fun project(projectId: Long): GitLabProject = api.project(projectId)
    suspend fun issues(projectId: Long): List<GitLabIssue> = api.issues(projectId)
    suspend fun mergeRequests(projectId: Long): List<GitLabMergeRequest> = api.mergeRequests(projectId)
    suspend fun commits(projectId: Long): List<GitLabCommit> = api.commits(projectId)
    suspend fun branches(projectId: Long): List<GitLabBranch> = api.branches(projectId)
}
