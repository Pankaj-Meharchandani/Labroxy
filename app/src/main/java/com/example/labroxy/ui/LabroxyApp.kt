package com.example.labroxy.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labroxy.ui.components.LoadingSessionScreen
import com.example.labroxy.ui.dashboard.DashboardScreen
import com.example.labroxy.ui.navigation.Screen
import com.example.labroxy.ui.navigation.WorkSection
import com.example.labroxy.ui.screens.AboutScreen
import com.example.labroxy.ui.screens.GroupScreen
import com.example.labroxy.ui.screens.ProjectScreen
import com.example.labroxy.ui.screens.SignInScreen
import com.example.labroxy.ui.screens.UserScreen
import com.example.labroxy.ui.screens.detail.WorkDetailScreen
import com.example.labroxy.ui.theme.LabroxyTheme

@Composable
fun LabroxyApp(
    deepLinkUrl: String? = null,
    viewModel: LabroxyViewModel = viewModel()
) {
    val session by viewModel.session.collectAsState()
    val dashboard by viewModel.dashboard.collectAsState()
    val project by viewModel.project.collectAsState()
    val group by viewModel.group.collectAsState()
    val detail by viewModel.detail.collectAsState()
    val userState by viewModel.userState.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()
    var screen by remember { mutableStateOf(Screen.Loading) }
    var section by remember { mutableStateOf(WorkSection.Home) }
    var handledDeepLinkUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(session.isLoaded, session.isReady) {
        screen = when {
            !session.isLoaded -> Screen.Loading
            session.isReady -> Screen.Dashboard
            else -> Screen.SignIn
        }
        if (session.isLoaded && !session.isReady) section = WorkSection.Home
    }

    LaunchedEffect(deepLinkUrl, session.isReady) {
        val url = deepLinkUrl ?: return@LaunchedEffect
        if (session.isReady && handledDeepLinkUrl != url && viewModel.loadGitLabLink(url)) {
            handledDeepLinkUrl = url
            screen = Screen.Detail
        }
    }

    BackHandler(enabled = screen in listOf(Screen.Project, Screen.Group, Screen.Detail, Screen.User) || section != WorkSection.Home) {
        if (screen in listOf(Screen.Project, Screen.Group, Screen.Detail, Screen.User)) {
            screen = Screen.Dashboard
        } else {
            section = WorkSection.Home
        }
    }

    LabroxyTheme(settings.themeMode) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AnimatedContent(targetState = screen, label = "screen") { target ->
                when (target) {
                    Screen.Loading -> LoadingSessionScreen()
                    Screen.SignIn -> SignInScreen(
                        defaultHost = session.host,
                        onConnect = viewModel::saveSession
                    )
                    Screen.Dashboard -> DashboardScreen(
                        state = dashboard,
                        section = section,
                        query = query,
                        onSectionChange = { section = it },
                        onQueryChange = viewModel::setSearchQuery,
                        onProjectClick = {
                            viewModel.loadProject(it.id)
                            screen = Screen.Project
                        },
                        onGroupClick = {
                            viewModel.loadGroup(it)
                            screen = Screen.Group
                        },
                        onIssueClick = {
                            it.projectId?.let { projectId ->
                                viewModel.loadIssue(projectId, it.iid)
                                screen = Screen.Detail
                            }
                        },
                        onMergeRequestClick = {
                            it.projectId?.let { projectId ->
                                viewModel.loadMergeRequest(projectId, it.iid)
                                screen = Screen.Detail
                            }
                        },
                        onTodoClick = {
                            viewModel.loadTodo(it)
                            screen = Screen.Detail
                        },
                        onEventClick = {
                            viewModel.loadEvent(it)
                            if (it.projectId != null && it.targetIid != null && it.targetType in listOf("Issue", "WorkItem", "MergeRequest")) {
                                screen = Screen.Detail
                            }
                        },
                        settings = settings,
                        onThemeModeChange = viewModel::setThemeMode,
                        onHomeUiStyleChange = viewModel::setHomeUiStyle,
                        onPushNotificationsChange = viewModel::setPushNotifications,
                        onToggleProjectsTab = viewModel::setShowProjectsTab,
                        onToggleGroupsTab = viewModel::setShowGroupsTab,
                        onToggleAssignedTab = viewModel::setShowAssignedTab,
                        onToggleMergeRequestsTab = viewModel::setShowMergeRequestsTab,
                        onToggleTodosTab = viewModel::setShowTodosTab,
                        onToggleNotificationsTab = viewModel::setShowNotificationsTab,
                        onToggleActivities = viewModel::setShowActivities,
                        onAboutClick = { screen = Screen.About },
                        onSignOut = viewModel::signOut
                    )
                    Screen.About -> AboutScreen(
                        onBack = { screen = Screen.Dashboard }
                    )
                    Screen.Project -> ProjectScreen(
                        state = project,
                        onBack = { screen = Screen.Dashboard },
                        onIssueClick = {
                            it.projectId?.let { projectId ->
                                viewModel.loadIssue(projectId, it.iid)
                                screen = Screen.Detail
                            }
                        },
                        onMergeRequestClick = {
                            it.projectId?.let { projectId ->
                                viewModel.loadMergeRequest(projectId, it.iid)
                                screen = Screen.Detail
                            }
                        }
                    )
                    Screen.Group -> GroupScreen(
                        state = group,
                        onBack = { screen = Screen.Dashboard },
                        onProjectClick = {
                            viewModel.loadProject(it.id)
                            screen = Screen.Project
                        },
                        onGroupClick = {
                            viewModel.loadGroup(it)
                        },
                        onIssueClick = {
                            it.projectId?.let { projectId ->
                                viewModel.loadIssue(projectId, it.iid)
                                screen = Screen.Detail
                            }
                        }
                    )
                    Screen.Detail -> WorkDetailScreen(
                        state = detail,
                        session = session,
                        onBack = { screen = screen.takeIf { it != Screen.Detail } ?: Screen.Dashboard },
                        onIssueReferenceClick = viewModel::loadIssueReference,
                        onGitLabLinkClick = { url ->
                            viewModel.loadGitLabLink(url).also { handled ->
                                if (handled) screen = Screen.Detail
                            }
                        },
                        onComment = viewModel::addComment,
                        onUpdateIssue = viewModel::updateIssueStatusAndLabels,
                        onUserClick = { username ->
                            viewModel.loadUser(username)
                            screen = Screen.User
                        },
                        uploadMarkdown = uploadState,
                        onUploadFile = { projectId, uri -> viewModel.uploadFile(projectId, uri) },
                        onClearUpload = viewModel::clearUploadState,
                        onToggleReaction = viewModel::toggleReaction,
                        currentUserId = (dashboard as? LoadState.Success<DashboardData>)?.value?.user?.id ?: 0
                    )
                    Screen.User -> UserScreen(
                        state = userState,
                        onBack = { screen = Screen.Dashboard },
                        onIssueClick = {
                            it.projectId?.let { projectId ->
                                viewModel.loadIssue(projectId, it.iid)
                                screen = Screen.Detail
                            }
                        },
                        onMergeRequestClick = {
                            it.projectId?.let { projectId ->
                                viewModel.loadMergeRequest(projectId, it.iid)
                                screen = Screen.Detail
                            }
                        }
                    )
                }
            }
        }
    }
}
