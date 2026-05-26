package com.example.jetlab.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetlab.data.GitLabBoard
import com.example.jetlab.data.GitLabBranch
import com.example.jetlab.data.GitLabCommit
import com.example.jetlab.data.GitLabEvent
import com.example.jetlab.data.GitLabGroup
import com.example.jetlab.data.GitLabIssue
import com.example.jetlab.data.GitLabMergeRequest
import com.example.jetlab.data.GitLabNote
import com.example.jetlab.data.GitLabProject
import com.example.jetlab.data.GitLabRepository
import com.example.jetlab.data.GitLabSession
import com.example.jetlab.data.GitLabTodo
import com.example.jetlab.data.GitLabUser
import com.example.jetlab.data.AppSettings
import com.example.jetlab.data.SessionStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardData(
    val user: GitLabUser,
    val projects: List<GitLabProject> = emptyList(),
    val groups: List<GitLabGroup> = emptyList(),
    val workItems: List<GitLabIssue> = emptyList(),
    val assignedWorkItems: List<GitLabIssue> = emptyList(),
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
    val labels: List<String> = emptyList(),
    val notes: List<GitLabNote> = emptyList()
)

data class GroupData(
    val group: GitLabGroup,
    val projects: List<GitLabProject>,
    val subgroups: List<GitLabGroup> = emptyList(),
    val issues: List<GitLabIssue> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class JetLabViewModel(application: Application) : AndroidViewModel(application) {
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
                            emit(LoadState.Success(data))

                            data = data.copy(todos = runCatching { GitLabRepository(active).todos() }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(events = runCatching { GitLabRepository(active).events() }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(assignedWorkItems = runCatching { GitLabRepository(active).assignedIssues(data.user.id) }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(assignedMergeRequests = runCatching { GitLabRepository(active).assignedMergeRequests(data.user.id) }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(groups = runCatching { GitLabRepository(active).groups("") }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(workItems = runCatching { GitLabRepository(active).workItems("") }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(projects = runCatching { GitLabRepository(active).projects("") }.getOrDefault(emptyList()))
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
    private var projectJob: Job? = null
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
                ProjectData(
                    project = repo.project(projectId),
                    issues = repo.issues(projectId),
                    mergeRequests = repo.mergeRequests(projectId),
                    commits = repo.commits(projectId),
                    branches = repo.branches(projectId),
                    boards = repo.boards(projectId)
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

    fun loadIssue(projectId: Long, issueIid: Long) {
        loadDetail(DetailTarget.Issue(projectId, issueIid))
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
                when (target) {
                    is DetailTarget.Issue -> {
                        val issue = repo.issue(target.projectId, target.issueIid)
                        WorkDetailData(
                            target = target,
                            title = "#${issue.iid} ${issue.title}",
                            subtitle = "Issue in project ${target.projectId}",
                            state = issue.state,
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
                            notes = repo.mergeRequestNotes(target.projectId, target.mergeRequestIid)
                        )
                    }
                }
            }.fold(
                onSuccess = { _detail.value = LoadState.Success(it) },
                onFailure = { _detail.value = LoadState.Error(it.toFriendlyMessage()) }
            )
        }
    }
}
