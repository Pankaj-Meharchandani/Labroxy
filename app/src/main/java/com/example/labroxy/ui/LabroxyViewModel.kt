package com.example.labroxy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.labroxy.data.GitLabBoard
import com.example.labroxy.data.GitLabBranch
import com.example.labroxy.data.GitLabCommit
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
    val events: List<GitLabEvent> = emptyList()
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

data class WorkDetailData(
    val target: DetailTarget,
    val title: String,
    val subtitle: String,
    val state: String,
    val webUrl: String? = null,
    val labels: List<String> = emptyList(),
    val notes: List<GitLabNote> = emptyList()
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

                            data = data.copy(todos = runCatching { GitLabRepository(active).todos() }.getOrDefault(data.todos))
                            sessionStore.saveTodos(data.todos)
                            emit(LoadState.Success(data))

                            data = data.copy(events = runCatching { GitLabRepository(active).events() }.getOrDefault(data.events))
                            sessionStore.saveEvents(data.events)
                            emit(LoadState.Success(data))

                            data = data.copy(assignedWorkItems = runCatching { GitLabRepository(active).assignedIssues(data.user.id) }.getOrDefault(data.assignedWorkItems))
                            sessionStore.saveAssignedWorkItems(data.assignedWorkItems)
                            emit(LoadState.Success(data))

                            data = data.copy(assignedCompletedWorkItems = runCatching { GitLabRepository(active).assignedIssues(data.user.id, "closed") }.getOrDefault(data.assignedCompletedWorkItems))
                            sessionStore.saveAssignedCompletedWorkItems(data.assignedCompletedWorkItems)
                            emit(LoadState.Success(data))

                            data = data.copy(assignedMergeRequests = runCatching { GitLabRepository(active).assignedMergeRequests(data.user.id) }.getOrDefault(data.assignedMergeRequests))
                            sessionStore.saveAssignedMergeRequests(data.assignedMergeRequests)
                            emit(LoadState.Success(data))

                            data = data.copy(groups = runCatching { GitLabRepository(active).groups("") }.getOrDefault(data.groups))
                            sessionStore.saveGroups(data.groups)
                            emit(LoadState.Success(data))

                            data = data.copy(projects = runCatching { GitLabRepository(active).projects("") }.getOrDefault(data.projects))
                            sessionStore.saveProjects(data.projects)
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

    fun loadMergeRequest(projectId: Long, mergeRequestIid: Long) {
        loadDetail(DetailTarget.MergeRequest(projectId, mergeRequestIid))
    }

    fun loadTodo(todo: GitLabTodo) {
        val projectId = todo.project?.id ?: return
        val iid = todo.target?.iid ?: return
        when (todo.targetType) {
            "Issue" -> loadIssue(projectId, iid)
            "MergeRequest" -> loadMergeRequest(projectId, iid)
        }
    }

    fun loadEvent(event: GitLabEvent) {
        val projectId = event.projectId ?: return
        val iid = event.targetIid ?: return
        when (event.targetType) {
            "Issue" -> loadIssue(projectId, iid)
            "MergeRequest" -> loadMergeRequest(projectId, iid)
        }
    }

    fun addComment(body: String) {
        val current = (_detail.value as? LoadState.Success)?.value ?: return
        if (body.isBlank()) return
        viewModelScope.launch {
            val active = session.value
            runCatching {
                val repo = GitLabRepository(active)
                when (val target = current.target) {
                    is DetailTarget.Issue -> repo.addIssueNote(target.projectId, target.issueIid, body)
                    is DetailTarget.MergeRequest -> repo.addMergeRequestNote(target.projectId, target.mergeRequestIid, body)
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

    private fun loadDetail(target: DetailTarget) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            val active = session.value
            _detail.value = LoadState.Loading
            runCatching {
                val repo = GitLabRepository(active)
                detailDataForTarget(repo, target)
            }.fold(
                onSuccess = { _detail.value = LoadState.Success(it) },
                onFailure = { _detail.value = LoadState.Error(it.toFriendlyMessage()) }
            )
        }
    }

    private suspend fun detailDataForTarget(repo: GitLabRepository, target: DetailTarget): WorkDetailData =
        when (target) {
            is DetailTarget.Issue -> {
                val issue = repo.issue(target.projectId, target.issueIid)
                WorkDetailData(
                    target = target,
                    title = "#${issue.iid} ${issue.title}",
                    subtitle = "Issue in project ${target.projectId}",
                    state = issue.state,
                    webUrl = issue.webUrl,
                    labels = issue.labels,
                    notes = repo.issueNotes(target.projectId, target.issueIid)
                )
            }
            is DetailTarget.MergeRequest -> {
                val mr = repo.mergeRequest(target.projectId, target.mergeRequestIid)
                WorkDetailData(
                    target = target,
                    title = "!${mr.iid} ${mr.title}",
                    subtitle = "${mr.sourceBranch} into ${mr.targetBranch}",
                    state = mr.state,
                    webUrl = mr.webUrl,
                    notes = repo.mergeRequestNotes(target.projectId, target.mergeRequestIid)
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
            events = cache.events
        )
}
