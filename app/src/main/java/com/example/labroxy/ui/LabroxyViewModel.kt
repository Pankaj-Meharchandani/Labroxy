package com.example.labroxy.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.labroxy.data.GitLabAwardEmoji
import com.example.labroxy.data.GitLabBoard
import com.example.labroxy.data.GitLabBranch
import com.example.labroxy.data.GitLabCommit
import com.example.labroxy.data.GitLabDiscussion
import com.example.labroxy.data.GitLabEvent
import com.example.labroxy.data.GitLabGroup
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabMergeRequest
import com.example.labroxy.data.GitLabNote
import com.example.labroxy.data.GitLabProject
import com.example.labroxy.data.GitLabRepository
import com.example.labroxy.data.GitLabSession
import com.example.labroxy.data.GitLabTodo
import com.example.labroxy.data.GitLabUser
import com.example.labroxy.data.AppSettings
import com.example.labroxy.data.CachedDashboard
import com.example.labroxy.data.SessionStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardData(
    val user: GitLabUser,
    val projects: List<GitLabProject> = emptyList(),
    val groups: List<GitLabGroup> = emptyList(),
    val assignedWorkItems: List<GitLabIssue> = emptyList(),
    val assignedCompletedWorkItems: List<GitLabIssue> = emptyList(),
    val assignedMergeRequests: List<GitLabMergeRequest> = emptyList(),
    val todos: List<GitLabTodo> = emptyList(),
    val doneTodos: List<GitLabTodo> = emptyList(),
    val events: List<GitLabEvent> = emptyList(),
    val projectEvents: List<GitLabEvent> = emptyList()
)

data class ProjectData(
    val project: GitLabProject,
    val issues: List<GitLabIssue>,
    val mergeRequests: List<GitLabMergeRequest>,
    val commits: List<GitLabCommit>,
    val branches: List<GitLabBranch>,
    val boards: List<GitLabBoard>
)

sealed interface DetailTarget {
    data class Issue(val projectId: Long, val issueIid: Long) : DetailTarget
    data class MergeRequest(val projectId: Long, val mergeRequestIid: Long) : DetailTarget
}

private data class GitLabLinkTarget(
    val projectPath: String,
    val targetKind: String,
    val iid: Long
)

data class WorkDetailData(
    val target: DetailTarget,
    val title: String,
    val subtitle: String,
    val state: String,
    val description: String? = null,
    val webUrl: String? = null,
    val labels: List<String> = emptyList(),
    val discussions: List<GitLabDiscussion> = emptyList(),
    val awardEmoji: List<GitLabAwardEmoji> = emptyList()
)

data class GroupData(
    val group: GitLabGroup,
    val projects: List<GitLabProject>,
    val subgroups: List<GitLabGroup> = emptyList(),
    val issues: List<GitLabIssue> = emptyList()
)

data class UserData(
    val user: GitLabUser,
    val issues: List<GitLabIssue> = emptyList(),
    val mergeRequests: List<GitLabMergeRequest> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class LabroxyViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application)

    val session: StateFlow<GitLabSession> = sessionStore.session.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GitLabSession()
    )

    private val query = MutableStateFlow("")
    val searchQuery: StateFlow<String> = query.asStateFlow()
    val settings: StateFlow<AppSettings> = sessionStore.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppSettings()
    )

    val dashboard: StateFlow<LoadState<DashboardData>> =
        session
            .flatMapLatest { active ->
                flow {
                    if (!active.isReady) {
                        emit(
                            if (active.isLoaded) {
                                LoadState.Error("Connect to GitLab to see your projects.")
                            } else {
                                LoadState.Loading
                            }
                        )
                        return@flow
                    }
                    emit(LoadState.Loading)
                    runCatching {
                        val repo = GitLabRepository(active)
                        val user = repo.me()
                        DashboardData(user)
                    }.fold(
                        onSuccess = { initial ->
                            var data = initial
                            val cached = sessionStore.cachedDashboard.first()
                            data = data.withCache(cached)
                            emit(LoadState.Success(data))

                            val repo = GitLabRepository(active)

                            // 1. Todos (quick)
                            data = data.copy(todos = runCatching { repo.todos("pending") }.getOrDefault(data.todos))
                            sessionStore.saveTodos(data.todos)
                            emit(LoadState.Success(data))

                            data = data.copy(doneTodos = runCatching { repo.todos("done") }.getOrDefault(data.doneTodos))
                            sessionStore.saveDoneTodos(data.doneTodos)
                            emit(LoadState.Success(data))

                            // 2. Global Events
                            data = data.copy(events = runCatching { repo.events() }.getOrDefault(data.events))
                            sessionStore.saveEvents(data.events)
                            emit(LoadState.Success(data))

                            // 3. Projects & Assigned (needed for project events)
                            data = data.copy(assignedWorkItems = runCatching { repo.assignedIssues(data.user.id) }.getOrDefault(data.assignedWorkItems))
                            sessionStore.saveAssignedWorkItems(data.assignedWorkItems)
                            emit(LoadState.Success(data))

                            data = data.copy(projects = runCatching { repo.projects("") }.getOrDefault(data.projects))
                            sessionStore.saveProjects(data.projects)
                            emit(LoadState.Success(data))

                            // 4. Project Events (intensive)
                            val pids = (data.assignedWorkItems.mapNotNull { it.projectId } + data.projects.take(15).map { it.id }).distinct().take(25)
                            val allProjectEvents = java.util.Collections.synchronizedList(mutableListOf<GitLabEvent>())
                            coroutineScope {
                                pids.forEach { pid ->
                                    async {
                                        runCatching { repo.projectEvents(pid) }.getOrNull()?.let { allProjectEvents.addAll(it) }
                                    }
                                }
                            }
                            data = data.copy(projectEvents = allProjectEvents.toList().distinctBy { it.id })
                            sessionStore.saveProjectEvents(data.projectEvents)
                            emit(LoadState.Success(data))

                            // 5. Rest of the data
                            data = data.copy(assignedCompletedWorkItems = runCatching { repo.assignedIssues(data.user.id, "closed") }.getOrDefault(data.assignedCompletedWorkItems))
                            sessionStore.saveAssignedCompletedWorkItems(data.assignedCompletedWorkItems)
                            emit(LoadState.Success(data))

                            data = data.copy(assignedMergeRequests = runCatching { repo.assignedMergeRequests(data.user.id) }.getOrDefault(data.assignedMergeRequests))
                            sessionStore.saveAssignedMergeRequests(data.assignedMergeRequests)
                            emit(LoadState.Success(data))

                            data = data.copy(groups = runCatching { repo.groups("") }.getOrDefault(data.groups))
                            sessionStore.saveGroups(data.groups)
                            emit(LoadState.Success(data))
                        },
                        onFailure = { emit(LoadState.Error(it.toFriendlyMessage())) }
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LoadState.Loading)

    private val _project = MutableStateFlow<LoadState<ProjectData>>(LoadState.Loading)
    val project: StateFlow<LoadState<ProjectData>> = _project.asStateFlow()
    private val _detail = MutableStateFlow<LoadState<WorkDetailData>>(LoadState.Loading)
    val detail: StateFlow<LoadState<WorkDetailData>> = _detail.asStateFlow()
    private val _group = MutableStateFlow<LoadState<GroupData>>(LoadState.Loading)
    val group: StateFlow<LoadState<GroupData>> = _group.asStateFlow()
    private val _userState = MutableStateFlow<LoadState<UserData>>(LoadState.Loading)
    val userState: StateFlow<LoadState<UserData>> = _userState.asStateFlow()
    private var projectJob: Job? = null
    private var userJob: Job? = null
    private var detailJob: Job? = null
    private var groupJob: Job? = null

    fun setSearchQuery(value: String) {
        query.value = value
    }

    fun setThemeMode(value: String) {
        viewModelScope.launch {
            sessionStore.saveThemeMode(value)
        }
    }

    fun setPushNotifications(enabled: Boolean) {
        viewModelScope.launch {
            sessionStore.savePushNotifications(enabled)
        }
    }

    fun saveSession(host: String, token: String) {
        viewModelScope.launch {
            sessionStore.save(host, token)
            query.value = query.value
        }
    }

    fun signOut() {
        viewModelScope.launch {
            sessionStore.clear()
        }
    }

    fun loadProject(projectId: Long) {
        projectJob?.cancel()
        projectJob = viewModelScope.launch {
            val active = session.value
            if (!active.isReady) {
                _project.value = LoadState.Error("Connect to GitLab first.")
                return@launch
            }
            _project.value = LoadState.Loading
            runCatching {
                val repo = GitLabRepository(active)
                val project = repo.project(projectId)
                ProjectData(
                    project = project,
                    issues = runCatching { repo.issues(projectId) }.getOrDefault(emptyList()),
                    mergeRequests = runCatching { repo.mergeRequests(projectId) }.getOrDefault(emptyList()),
                    commits = runCatching { repo.commits(projectId) }.getOrDefault(emptyList()),
                    branches = runCatching { repo.branches(projectId) }.getOrDefault(emptyList()),
                    boards = runCatching { repo.boards(projectId) }.getOrDefault(emptyList())
                )
            }.fold(
                onSuccess = { _project.value = LoadState.Success(it) },
                onFailure = { _project.value = LoadState.Error(it.toFriendlyMessage()) }
            )
        }
    }

    fun loadGroup(group: GitLabGroup) {
        groupJob?.cancel()
        groupJob = viewModelScope.launch {
            val active = session.value
            _group.value = LoadState.Loading
            runCatching {
                val repo = GitLabRepository(active)
                GroupData(
                    group = group,
                    projects = repo.groupProjects(group.id),
                    subgroups = runCatching { repo.subgroups(group.id) }.getOrDefault(emptyList()),
                    issues = runCatching { repo.groupIssues(group.id) }.getOrDefault(emptyList())
                )
            }.fold(
                onSuccess = { _group.value = LoadState.Success(it) },
                onFailure = { _group.value = LoadState.Error(it.toFriendlyMessage()) }
            )
        }
    }

    fun loadUser(username: String) {
        userJob?.cancel()
        userJob = viewModelScope.launch {
            val active = session.value
            if (!active.isReady) {
                _userState.value = LoadState.Error("Connect to GitLab first.")
                return@launch
            }
            _userState.value = LoadState.Loading
            runCatching {
                val repo = GitLabRepository(active)
                val user = repo.getUserByUsername(username) ?: throw Exception("User @$username not found")
                val issues = runCatching { repo.assignedIssues(user.id) }.getOrDefault(emptyList())
                val mergeRequests = runCatching { repo.assignedMergeRequests(user.id) }.getOrDefault(emptyList())
                UserData(user, issues, mergeRequests)
            }.fold(
                onSuccess = { _userState.value = LoadState.Success(it) },
                onFailure = { _userState.value = LoadState.Error(it.toFriendlyMessage()) }
            )
        }
    }

    fun loadIssue(projectId: Long, issueIid: Long) {
        loadDetail(DetailTarget.Issue(projectId, issueIid))
    }

    fun loadIssueReference(projectPath: String?, fallbackProjectId: Long, issueIid: Long) {
        if (projectPath.isNullOrBlank()) {
            loadIssue(fallbackProjectId, issueIid)
            return
        }
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            val active = session.value
            _detail.value = LoadState.Loading
            runCatching {
                val repo = GitLabRepository(active)
                val projectId = repo.project(projectPath).id
                detailDataForTarget(repo, DetailTarget.Issue(projectId, issueIid))
            }.fold(
                onSuccess = { _detail.value = LoadState.Success(it) },
                onFailure = { _detail.value = LoadState.Error(it.toFriendlyMessage()) }
            )
        }
    }

    fun loadGitLabLink(url: String): Boolean {
        val active = session.value
        if (!active.isReady) return false
        val linkTarget = url.toGitLabLinkTarget(active.host) ?: return false

        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _detail.value = LoadState.Loading
            runCatching {
                val repo = GitLabRepository(active)
                val projectId = repo.project(linkTarget.projectPath).id
                val target = when (linkTarget.targetKind) {
                    "merge_requests" -> DetailTarget.MergeRequest(projectId, linkTarget.iid)
                    "issues", "work_items" -> DetailTarget.Issue(projectId, linkTarget.iid)
                    else -> throw IllegalArgumentException("Unsupported GitLab link")
                }
                detailDataForTarget(repo, target)
            }.fold(
                onSuccess = { _detail.value = LoadState.Success(it) },
                onFailure = { _detail.value = LoadState.Error(it.toFriendlyMessage()) }
            )
        }
        return true
    }

    fun loadMergeRequest(projectId: Long, mergeRequestIid: Long) {
        loadDetail(DetailTarget.MergeRequest(projectId, mergeRequestIid))
    }

    fun loadTodo(todo: GitLabTodo) {
        val projectId = todo.project?.id ?: return
        val iid = todo.target?.iid ?: return
        when (todo.targetType) {
            "Issue", "WorkItem" -> loadIssue(projectId, iid)
            "MergeRequest" -> loadMergeRequest(projectId, iid)
        }
    }

    fun loadEvent(event: GitLabEvent) {
        val projectId = event.projectId ?: return
        val iid = event.targetIid ?: return
        when (event.targetType) {
            "Issue", "WorkItem" -> loadIssue(projectId, iid)
            "MergeRequest" -> loadMergeRequest(projectId, iid)
        }
    }

    fun addComment(body: String, discussionId: String? = null) {
        val current = (_detail.value as? LoadState.Success)?.value ?: return
        if (body.isBlank()) return
        viewModelScope.launch {
            val active = session.value
            runCatching {
                val repo = GitLabRepository(active)
                when (val target = current.target) {
                    is DetailTarget.Issue -> {
                        if (discussionId == null) {
                            repo.addIssueDiscussion(target.projectId, target.issueIid, body)
                        } else {
                            repo.addIssueDiscussionNote(target.projectId, target.issueIid, discussionId, body)
                        }
                    }
                    is DetailTarget.MergeRequest -> {
                        if (discussionId == null) {
                            repo.addMergeRequestDiscussion(target.projectId, target.mergeRequestIid, body)
                        } else {
                            repo.addMergeRequestDiscussionNote(target.projectId, target.mergeRequestIid, discussionId, body)
                        }
                    }
                }
            }.onSuccess {
                loadDetail(current.target)
            }.onFailure {
                _detail.value = LoadState.Error(it.toFriendlyMessage())
            }
        }
    }

    fun updateIssueStatusAndLabels(close: Boolean, labelsCsv: String) {
        val current = (_detail.value as? LoadState.Success)?.value ?: return
        val target = current.target as? DetailTarget.Issue ?: return
        viewModelScope.launch {
            val active = session.value
            runCatching {
                GitLabRepository(active).updateIssue(
                    projectId = target.projectId,
                    issueIid = target.issueIid,
                    stateEvent = if (close) "close" else "reopen",
                    labels = labelsCsv
                )
            }.onSuccess {
                loadDetail(target)
            }.onFailure {
                _detail.value = LoadState.Error(it.toFriendlyMessage())
            }
        }
    }

    fun uploadFile(projectId: Long, uri: Uri) {
        viewModelScope.launch {
            val active = session.value
            runCatching {
                val context = getApplication<Application>()
                val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } ?: "upload.bin"
                val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: throw Exception("Could not read file")
                
                GitLabRepository(active).uploadFile(projectId, bytes, fileName)
            }.onSuccess { upload ->
                // We'll update a state to indicate upload success and the markdown to insert
                _uploadState.value = upload.markdown
            }.onFailure {
                // handle failure
            }
        }
    }

    private val _uploadState = MutableStateFlow<String?>(null)
    val uploadState = _uploadState.asStateFlow()

    fun clearUploadState() {
        _uploadState.value = null
    }

    fun toggleReaction(note: GitLabNote?, emojiName: String) {
        val current = (_detail.value as? LoadState.Success)?.value ?: return
        val target = current.target
        val path = if (note != null) {
            when (target) {
                is DetailTarget.Issue -> "projects/${target.projectId}/issues/${target.issueIid}/notes/${note.id}"
                is DetailTarget.MergeRequest -> "projects/${target.projectId}/merge_requests/${target.mergeRequestIid}/notes/${note.id}"
            }
        } else {
            when (target) {
                is DetailTarget.Issue -> "projects/${target.projectId}/issues/${target.issueIid}"
                is DetailTarget.MergeRequest -> "projects/${target.projectId}/merge_requests/${target.mergeRequestIid}"
            }
        }
        
        val awardEmoji = note?.awardEmoji ?: current.awardEmoji
        val dashboardData = (dashboard.value as? LoadState.Success)?.value
        val myReaction = awardEmoji.find { it.name == emojiName && it.user.username == dashboardData?.user?.username }

        viewModelScope.launch {
            val active = session.value
            runCatching {
                val repo = GitLabRepository(active)
                if (myReaction != null) {
                    repo.deleteAwardEmoji(path, myReaction.id)
                } else {
                    repo.addAwardEmoji(path, emojiName)
                }
            }.onSuccess {
                loadDetail(target, silent = true)
            }
        }
    }

    private fun loadDetail(target: DetailTarget, silent: Boolean = false) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            val active = session.value
            if (!silent) _detail.value = LoadState.Loading
            runCatching {
                val repo = GitLabRepository(active)
                detailDataForTarget(repo, target)
            }.fold(
                onSuccess = { _detail.value = LoadState.Success(it) },
                onFailure = { 
                    if (!silent) _detail.value = LoadState.Error(it.toFriendlyMessage()) 
                }
            )
        }
    }

    private suspend fun detailDataForTarget(repo: GitLabRepository, target: DetailTarget): WorkDetailData =
        when (target) {
            is DetailTarget.Issue -> {
                val issue = repo.issue(target.projectId, target.issueIid)
                val emoji = runCatching { repo.getAwardEmoji("projects/${target.projectId}/issues/${target.issueIid}") }.getOrDefault(emptyList())
                WorkDetailData(
                    target = target,
                    title = "#${issue.iid} ${issue.title}",
                    subtitle = "Issue in project ${target.projectId}",
                    state = issue.state,
                    description = issue.description,
                    webUrl = issue.webUrl,
                    labels = issue.labels,
                    discussions = repo.issueDiscussions(target.projectId, target.issueIid),
                    awardEmoji = emoji
                )
            }
            is DetailTarget.MergeRequest -> {
                val mr = repo.mergeRequest(target.projectId, target.mergeRequestIid)
                val emoji = runCatching { repo.getAwardEmoji("projects/${target.projectId}/merge_requests/${target.mergeRequestIid}") }.getOrDefault(emptyList())
                WorkDetailData(
                    target = target,
                    title = "!${mr.iid} ${mr.title}",
                    subtitle = "${mr.sourceBranch} into ${mr.targetBranch}",
                    state = mr.state,
                    description = mr.description,
                    webUrl = mr.webUrl,
                    discussions = repo.mergeRequestDiscussions(target.projectId, target.mergeRequestIid),
                    awardEmoji = emoji
                )
            }
        }

    private fun DashboardData.withCache(cache: CachedDashboard): DashboardData =
        copy(
            projects = cache.projects,
            groups = cache.groups,
            assignedWorkItems = cache.assignedWorkItems,
            assignedCompletedWorkItems = cache.assignedCompletedWorkItems,
            assignedMergeRequests = cache.assignedMergeRequests,
            todos = cache.todos,
            events = cache.events,
            projectEvents = cache.projectEvents
        )
}

private fun String.toGitLabLinkTarget(configuredHost: String): GitLabLinkTarget? {
    val uri = runCatching { Uri.parse(this) }.getOrNull() ?: return null
    val linkHost = uri.host ?: return null
    val expectedHost = runCatching { Uri.parse(configuredHost.trim()).host }.getOrNull()
        ?: configuredHost.trim().removePrefix("https://").removePrefix("http://").substringBefore("/")
    
    // Support gitlab.com even if not configured as primary, or the configured host
    val isGitLabHost = linkHost.equals("gitlab.com", ignoreCase = true) ||
                      linkHost.equals(expectedHost, ignoreCase = true) ||
                      linkHost.endsWith(".e.foundation", ignoreCase = true)
    
    if (!isGitLabHost) {
        // If it's a different host, we still try to parse it if it looks like GitLab
        // but it will only be loadable if the host matches the session in the end.
    }

    val segments = uri.pathSegments
    val separatorIndex = segments.indexOf("-")
    
    val targetKind: String
    val iid: Long
    val projectPath: String

    if (separatorIndex >= 0) {
        if (separatorIndex == 0 || separatorIndex + 2 >= segments.size) return null
        targetKind = segments[separatorIndex + 1]
        iid = segments[separatorIndex + 2].toLongOrNull() ?: return null
        projectPath = segments.take(separatorIndex).joinToString("/")
    } else {
        // Fallback for URLs without the '-' separator (e.g. some self-hosted or older versions)
        val issuesIndex = segments.lastIndexOf("issues")
        val mrsIndex = segments.lastIndexOf("merge_requests")
        val workItemsIndex = segments.lastIndexOf("work_items")
        
        val keywordIndex = maxOf(issuesIndex, mrsIndex, workItemsIndex)
        if (keywordIndex <= 0 || keywordIndex + 1 >= segments.size) return null
        
        targetKind = segments[keywordIndex]
        iid = segments[keywordIndex + 1].toLongOrNull() ?: return null
        projectPath = segments.take(keywordIndex).joinToString("/")
    }

    // Final safety check: if the host doesn't match configured one, 
    // we only return a target if we are sure it's a GitLab item.
    if (!linkHost.equals(expectedHost, ignoreCase = true)) {
        // If host mismatch, we can only open it if the user switches session.
        // For now, return null to avoid attempting to load from wrong API.
        return null
    }

    return GitLabLinkTarget(projectPath = projectPath, targetKind = targetKind, iid = iid)
}
