package com.example.labroxy.data

class GitLabRepository(
    private val session: GitLabSession
) {
    private val api = GitLabApi(session.host) { session.token }

    suspend fun me(): GitLabUser = api.currentUser()
    suspend fun getUserByUsername(username: String): GitLabUser? = api.getUserByUsername(username)
    suspend fun projects(query: String): List<GitLabProject> = api.projects(query)
    suspend fun groups(query: String): List<GitLabGroup> = api.groups(query)
    suspend fun todos(state: String? = "pending", maxPages: Int = 1): List<GitLabTodo> = api.todos(state, maxPages)
    suspend fun assignedIssues(userId: Long, state: String = "opened"): List<GitLabIssue> = api.assignedIssues(userId, state)
    suspend fun assignedMergeRequests(userId: Long): List<GitLabMergeRequest> = api.assignedMergeRequests(userId)
    suspend fun events(): List<GitLabEvent> = api.events()
    suspend fun projectEvents(projectId: Long): List<GitLabEvent> = api.projectEvents(projectId)
    suspend fun project(projectId: Long): GitLabProject = api.project(projectId)
    suspend fun project(projectPath: String): GitLabProject = api.project(projectPath)
    suspend fun groupProjects(groupId: Long): List<GitLabProject> = api.groupProjects(groupId)
    suspend fun subgroups(groupId: Long): List<GitLabGroup> = api.subgroups(groupId)
    suspend fun groupIssues(groupId: Long): List<GitLabIssue> = api.groupIssues(groupId)
    suspend fun issue(projectId: Long, issueIid: Long): GitLabIssue = api.issue(projectId, issueIid)
    suspend fun mergeRequest(projectId: Long, mergeRequestIid: Long): GitLabMergeRequest = api.mergeRequest(projectId, mergeRequestIid)
    suspend fun issueDiscussions(projectId: Long, issueIid: Long): List<GitLabDiscussion> = api.issueDiscussions(projectId, issueIid)
    suspend fun mergeRequestDiscussions(projectId: Long, mergeRequestIid: Long): List<GitLabDiscussion> =
        api.mergeRequestDiscussions(projectId, mergeRequestIid)
    suspend fun addIssueDiscussion(projectId: Long, issueIid: Long, body: String): GitLabDiscussion =
        api.addIssueDiscussion(projectId, issueIid, body)
    suspend fun addMergeRequestDiscussion(projectId: Long, mergeRequestIid: Long, body: String): GitLabDiscussion =
        api.addMergeRequestDiscussion(projectId, mergeRequestIid, body)
    suspend fun addIssueDiscussionNote(projectId: Long, issueIid: Long, discussionId: String, body: String): GitLabNote =
        api.addIssueDiscussionNote(projectId, issueIid, discussionId, body)
    suspend fun addMergeRequestDiscussionNote(
        projectId: Long,
        mergeRequestIid: Long,
        discussionId: String,
        body: String
    ): GitLabNote =
        api.addMergeRequestDiscussionNote(projectId, mergeRequestIid, discussionId, body)
    suspend fun updateIssue(projectId: Long, issueIid: Long, stateEvent: String?, labels: String?): GitLabIssue =
        api.updateIssue(projectId, issueIid, stateEvent, labels)
    suspend fun issues(projectId: Long): List<GitLabIssue> = api.issues(projectId)
    suspend fun mergeRequests(projectId: Long): List<GitLabMergeRequest> = api.mergeRequests(projectId)
    suspend fun commits(projectId: Long): List<GitLabCommit> = api.commits(projectId)
    suspend fun branches(projectId: Long): List<GitLabBranch> = api.branches(projectId)
    suspend fun boards(projectId: Long): List<GitLabBoard> = api.boards(projectId)

    suspend fun uploadFile(projectId: Long, file: ByteArray, fileName: String): GitLabUpload =
        api.uploadFile(projectId, file, fileName)

    suspend fun getAwardEmoji(path: String): List<GitLabAwardEmoji> =
        api.getAwardEmoji(path)

    suspend fun addAwardEmoji(path: String, emojiName: String): GitLabAwardEmoji =
        api.addAwardEmoji(path, emojiName)

    suspend fun deleteAwardEmoji(path: String, emojiId: Long) =
        api.deleteAwardEmoji(path, emojiId)
}
