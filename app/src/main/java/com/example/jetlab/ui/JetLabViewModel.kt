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
import com.example.jetlab.data.GitLabProject
import com.example.jetlab.data.GitLabRepository
import com.example.jetlab.data.GitLabSession
import com.example.jetlab.data.GitLabTodo
import com.example.jetlab.data.GitLabUser
import com.example.jetlab.data.SessionStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class JetLabViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application)

    val session: StateFlow<GitLabSession> = sessionStore.session.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GitLabSession()
    )

    private val query = MutableStateFlow("")
    val searchQuery: StateFlow<String> = query.asStateFlow()

    val dashboard: StateFlow<LoadState<DashboardData>> =
        combine(session, query.debounce(250).distinctUntilChanged()) { activeSession, search ->
            activeSession to search
        }
            .distinctUntilChanged()
            .flatMapLatest { (active, search) ->
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

                            data = data.copy(groups = runCatching { GitLabRepository(active).groups(search) }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(workItems = runCatching { GitLabRepository(active).workItems(search) }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))

                            data = data.copy(projects = runCatching { GitLabRepository(active).projects(search) }.getOrDefault(emptyList()))
                            emit(LoadState.Success(data))
                        },
                        onFailure = { emit(LoadState.Error(it.toFriendlyMessage())) }
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LoadState.Loading)

    private val _project = MutableStateFlow<LoadState<ProjectData>>(LoadState.Loading)
    val project: StateFlow<LoadState<ProjectData>> = _project.asStateFlow()
    private var projectJob: Job? = null

    fun setSearchQuery(value: String) {
        query.value = value
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
}
