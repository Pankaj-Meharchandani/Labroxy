@file:OptIn(ExperimentalLayoutApi::class)

package com.example.jetlab.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetlab.data.AppSettings
import com.example.jetlab.data.GitLabBoard
import com.example.jetlab.data.GitLabCommit
import com.example.jetlab.data.GitLabEvent
import com.example.jetlab.data.GitLabGroup
import com.example.jetlab.data.GitLabIssue
import com.example.jetlab.data.GitLabMergeRequest
import com.example.jetlab.data.GitLabProject
import com.example.jetlab.data.GitLabTodo
import com.example.jetlab.ui.theme.LabroxyTheme
import kotlinx.coroutines.launch

private enum class Screen { Loading, SignIn, Dashboard, Project, Group, Detail }

private enum class WorkSection(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Outlined.Home),
    Projects("Projects", Icons.Outlined.Folder),
    Groups("Groups", Icons.Outlined.AccountTree),
    WorkItems("Work items", Icons.Outlined.TaskAlt),
    Assigned("Assigned", Icons.Outlined.Tag),
    MergeRequests("Merge requests", Icons.AutoMirrored.Outlined.MergeType),
    Todos("To-Do List", Icons.Outlined.TaskAlt),
    Notifications("Notifications", Icons.Outlined.History),
    Settings("Settings", Icons.Outlined.Settings)
}

@Composable
fun LabroxyApp(viewModel: LabroxyViewModel = viewModel()) {
    val session by viewModel.session.collectAsState()
    val dashboard by viewModel.dashboard.collectAsState()
    val project by viewModel.project.collectAsState()
    val group by viewModel.group.collectAsState()
    val detail by viewModel.detail.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val settings by viewModel.settings.collectAsState()
    var screen by remember { mutableStateOf(Screen.Loading) }
    var section by remember { mutableStateOf(WorkSection.Home) }

    LaunchedEffect(session.isLoaded, session.isReady) {
        screen = when {
            !session.isLoaded -> Screen.Loading
            session.isReady -> Screen.Dashboard
            else -> Screen.SignIn
        }
        if (session.isLoaded && !session.isReady) section = WorkSection.Home
    }

    BackHandler(enabled = screen in listOf(Screen.Project, Screen.Group, Screen.Detail) || section != WorkSection.Home) {
        if (screen in listOf(Screen.Project, Screen.Group, Screen.Detail)) {
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
                        screen = Screen.Detail
                    },
                    settings = settings,
                    onThemeModeChange = viewModel::setThemeMode,
                    onPushNotificationsChange = viewModel::setPushNotifications,
                    onSignOut = viewModel::signOut
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
                    onBack = { screen = Screen.Dashboard },
                    onComment = viewModel::addComment,
                    onUpdateIssue = viewModel::updateIssueStatusAndLabels
                )
            }
        }
    }
    }
}

@Composable
private fun LoadingSessionScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SignInScreen(defaultHost: String, onConnect: (String, String) -> Unit) {
    var host by remember { mutableStateOf(defaultHost) }
    var token by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFEFE5), Color(0xFFFAF9F6), Color(0xFFE9F1F1))
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountTree,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(18.dp))
        Text("Labroxy", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text(
            "A calm, fast GitLab cockpit for projects, groups, work items, reviews, and notifications.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("GitLab host") },
                    leadingIcon = { Icon(Icons.Outlined.Code, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Personal access token") },
                    leadingIcon = { Icon(Icons.Outlined.Key, null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onConnect(host, token) },
                    enabled = host.isNotBlank() && token.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Connect")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreen(
    state: LoadState<DashboardData>,
    section: WorkSection,
    query: String,
    onSectionChange: (WorkSection) -> Unit,
    onQueryChange: (String) -> Unit,
    onProjectClick: (GitLabProject) -> Unit,
    onGroupClick: (GitLabGroup) -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onMergeRequestClick: (GitLabMergeRequest) -> Unit,
    onTodoClick: (GitLabTodo) -> Unit,
    onEventClick: (GitLabEvent) -> Unit,
    settings: AppSettings,
    onThemeModeChange: (String) -> Unit,
    onPushNotificationsChange: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            WorkDrawer(
                selected = section,
                state = state,
                onSectionChange = {
                    onSectionChange(it)
                    scope.launch { drawerState.close() }
                },
                onSignOut = onSignOut
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(section.label, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Outlined.Menu, contentDescription = "Open navigation")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onSectionChange(WorkSection.Notifications) }) {
                            Icon(Icons.Outlined.NotificationsNone, contentDescription = "Notifications")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (section !in listOf(WorkSection.Home, WorkSection.Settings)) {
                    item {
                        OutlinedTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            label = { Text("Search ${section.label.lowercase()}") },
                            leadingIcon = { Icon(Icons.Outlined.Search, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                when (state) {
                    LoadState.Loading -> item { LoadingBlock("Loading GitLab workspace") }
                    is LoadState.Error -> item { ErrorBlock(state.message) }
                    is LoadState.Success -> {
                        val data = state.value
                        when (section) {
                            WorkSection.Home -> homeItems(data, onSectionChange)
                            WorkSection.Projects -> projectItems(data.projects.filteredProjects(query), onProjectClick)
                            WorkSection.Groups -> groupItems(data.groups.filteredGroups(query), onGroupClick)
                            WorkSection.WorkItems -> issueItems(data.workItems.filteredIssues(query), "No open work items found.", onIssueClick)
                            WorkSection.Assigned -> issueItems(data.assignedWorkItems.filteredIssues(query), "Nothing is assigned to you.", onIssueClick)
                            WorkSection.MergeRequests -> mrItems(data.assignedMergeRequests.filteredMergeRequests(query), onMergeRequestClick)
                            WorkSection.Todos -> todoItems(data.todos.filteredTodos(query), onTodoClick)
                            WorkSection.Notifications -> eventItems(data.events.filteredEvents(query), onEventClick)
                            WorkSection.Settings -> settingsItems(settings, onThemeModeChange, onPushNotificationsChange)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkDrawer(
    selected: WorkSection,
    state: LoadState<DashboardData>,
    onSectionChange: (WorkSection) -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(292.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                "Your work",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
            WorkSection.entries.filterNot { it == WorkSection.Notifications }.forEach { section ->
                NavigationDrawerItem(
                    selected = selected == section,
                    onClick = { onSectionChange(section) },
                    icon = { Icon(section.icon, contentDescription = null) },
                    label = { Text(section.label, fontWeight = FontWeight.SemiBold) },
                    badge = { DrawerBadge(section, state) },
                    modifier = Modifier.height(46.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                        unselectedContainerColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                        selectedBadgeColor = MaterialTheme.colorScheme.primary,
                        unselectedBadgeColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(Modifier.weight(1f))
            NavigationDrawerItem(
                selected = false,
                onClick = onSignOut,
                icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                label = { Text("Sign out", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun DrawerBadge(section: WorkSection, state: LoadState<DashboardData>) {
    val data = (state as? LoadState.Success)?.value ?: return
    val count = when (section) {
        WorkSection.Home -> null
        WorkSection.Projects -> data.projects.size
        WorkSection.Groups -> data.groups.size
        WorkSection.WorkItems -> data.workItems.size
        WorkSection.Assigned -> data.assignedWorkItems.size
        WorkSection.MergeRequests -> data.assignedMergeRequests.size
        WorkSection.Todos -> data.todos.size
        WorkSection.Notifications -> data.events.size
        WorkSection.Settings -> null
    } ?: return
    if (count > 0) {
        Text("$count", fontWeight = FontWeight.Bold)
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.homeItems(
    data: DashboardData,
    onSectionChange: (WorkSection) -> Unit
) {
    item { WelcomeBlock(data) }
    item { HomeNavRow(WorkSection.Projects, data.projects.size, onSectionChange) }
    item { HomeNavRow(WorkSection.Groups, data.groups.size, onSectionChange) }
    item { HomeNavRow(WorkSection.WorkItems, data.workItems.size, onSectionChange) }
    item { HomeNavRow(WorkSection.Assigned, data.assignedWorkItems.size, onSectionChange) }
    item { HomeNavRow(WorkSection.MergeRequests, data.assignedMergeRequests.size, onSectionChange) }
    item { HomeNavRow(WorkSection.Todos, data.todos.size, onSectionChange) }
    item { HomeNavRow(WorkSection.Notifications, data.events.size, onSectionChange) }
}

private fun List<GitLabProject>.filteredProjects(query: String): List<GitLabProject> =
    if (query.isBlank()) this else filter {
        it.name.contains(query, ignoreCase = true) ||
            it.pathWithNamespace.contains(query, ignoreCase = true) ||
            it.description.orEmpty().contains(query, ignoreCase = true)
    }

private fun List<GitLabGroup>.filteredGroups(query: String): List<GitLabGroup> =
    if (query.isBlank()) this else filter {
        it.name.contains(query, ignoreCase = true) ||
            it.fullPath.contains(query, ignoreCase = true) ||
            it.description.orEmpty().contains(query, ignoreCase = true)
    }

private fun List<GitLabIssue>.filteredIssues(query: String): List<GitLabIssue> =
    if (query.isBlank()) this else filter {
        it.title.contains(query, ignoreCase = true) ||
            it.state.contains(query, ignoreCase = true) ||
            it.labels.any { label -> label.contains(query, ignoreCase = true) } ||
            it.author?.username.orEmpty().contains(query, ignoreCase = true)
    }

private fun List<GitLabMergeRequest>.filteredMergeRequests(query: String): List<GitLabMergeRequest> =
    if (query.isBlank()) this else filter {
        it.title.contains(query, ignoreCase = true) ||
            it.state.contains(query, ignoreCase = true) ||
            it.sourceBranch.contains(query, ignoreCase = true) ||
            it.targetBranch.contains(query, ignoreCase = true) ||
            it.author?.username.orEmpty().contains(query, ignoreCase = true)
    }

private fun List<GitLabTodo>.filteredTodos(query: String): List<GitLabTodo> =
    if (query.isBlank()) this else filter {
        it.target?.title.orEmpty().contains(query, ignoreCase = true) ||
            it.body.orEmpty().contains(query, ignoreCase = true) ||
            it.targetType.contains(query, ignoreCase = true) ||
            it.project?.name.orEmpty().contains(query, ignoreCase = true)
    }

private fun List<GitLabEvent>.filteredEvents(query: String): List<GitLabEvent> =
    if (query.isBlank()) this else filter {
        it.targetTitle.orEmpty().contains(query, ignoreCase = true) ||
            it.targetType.orEmpty().contains(query, ignoreCase = true) ||
            it.displayAction.contains(query, ignoreCase = true) ||
            it.author?.username.orEmpty().contains(query, ignoreCase = true)
    }

private fun androidx.compose.foundation.lazy.LazyListScope.settingsItems(
    settings: AppSettings,
    onThemeModeChange: (String) -> Unit,
    onPushNotificationsChange: (Boolean) -> Unit
) {
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("system" to "System", "light" to "Light", "dark" to "Dark").forEach { (value, label) ->
                        FilterChip(
                            selected = settings.themeMode == value,
                            onClick = { onThemeModeChange(value) },
                            label = { Text(label, maxLines = 1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Push notifications", fontWeight = FontWeight.Bold)
                    Text(
                        "Allow Labroxy to prepare notification delivery for GitLab updates.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.pushNotifications,
                    onCheckedChange = onPushNotificationsChange
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.projectItems(
    projects: List<GitLabProject>,
    onProjectClick: (GitLabProject) -> Unit
) {
    if (projects.isEmpty()) {
        item { EmptyBlock("No projects matched that search.") }
    } else {
        items(projects, key = { it.id }) { project ->
            ProjectCard(project, onClick = { onProjectClick(project) })
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.groupItems(groups: List<GitLabGroup>, onClick: (GitLabGroup) -> Unit) {
    if (groups.isEmpty()) {
        item { EmptyBlock("No groups matched that search.") }
    } else {
        items(groups, key = { it.id }) { group ->
            ListCard(
                icon = Icons.Outlined.AccountTree,
                title = group.name,
                meta = "${group.fullPath}${group.visibility?.let { " - $it" } ?: ""}",
                onClick = { onClick(group) }
            ) {
                group.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.issueItems(issues: List<GitLabIssue>, empty: String, onClick: (GitLabIssue) -> Unit) {
    if (issues.isEmpty()) {
        item { EmptyBlock(empty) }
    } else {
        items(issues, key = { it.id }) { IssueRow(it, onClick = { onClick(it) }) }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.mrItems(mrs: List<GitLabMergeRequest>, onClick: (GitLabMergeRequest) -> Unit = {}) {
    if (mrs.isEmpty()) {
        item { EmptyBlock("No merge requests are assigned to you.") }
    } else {
        items(mrs, key = { it.id }) { MergeRequestRow(it, onClick = { onClick(it) }) }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.todoItems(todos: List<GitLabTodo>, onClick: (GitLabTodo) -> Unit) {
    if (todos.isEmpty()) {
        item { EmptyBlock("Your to-do list is clear.") }
    } else {
        items(todos, key = { it.id }) { todo ->
            ListCard(
                icon = Icons.Outlined.TaskAlt,
                title = todo.target?.title ?: todo.body ?: todo.targetType,
                meta = "${todo.action} - ${todo.targetType}${todo.project?.name?.let { " in $it" } ?: ""}",
                onClick = { onClick(todo) }
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.eventItems(events: List<GitLabEvent>, onClick: (GitLabEvent) -> Unit) {
    if (events.isEmpty()) {
        item { EmptyBlock("No recent notifications yet.") }
    } else {
        items(events, key = { it.id }) { event ->
            ListCard(
                icon = Icons.Outlined.History,
                title = event.targetTitle ?: event.targetType ?: "GitLab activity",
                meta = "${event.author?.username ?: "Someone"} ${event.displayAction}",
                onClick = { onClick(event) }
            )
        }
    }
}

@Composable
private fun HomeNavRow(section: WorkSection, count: Int, onSectionChange: (WorkSection) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSectionChange(section) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(section.icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(14.dp))
            Text(section.label, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            Text("$count", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectScreen(
    state: LoadState<ProjectData>,
    onBack: () -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onMergeRequestClick: (GitLabMergeRequest) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Issues", "MRs", "Commits", "Boards")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Outlined.TaskAlt
                                    1 -> Icons.AutoMirrored.Outlined.MergeType
                                    2 -> Icons.Outlined.History
                                    else -> Icons.Outlined.AccountTree
                                },
                                contentDescription = label
                            )
                        },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        when (state) {
            LoadState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is LoadState.Error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                ErrorBlock(state.message)
            }
            is LoadState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ProjectHero(state.value.project, state.value.branches.size) }
                when (tab) {
                    0 -> issueItems(state.value.issues, "This project has no open issues.", onIssueClick)
                    1 -> mrItems(state.value.mergeRequests, onMergeRequestClick)
                    2 -> items(state.value.commits, key = { it.id }) { CommitRow(it) }
                    3 -> boardItems(state.value.boards)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.boardItems(boards: List<GitLabBoard>) {
    if (boards.isEmpty()) {
        item { EmptyBlock("This project has no issue boards.") }
    } else {
        items(boards, key = { it.id }) { board ->
            ListCard(
                icon = Icons.Outlined.AccountTree,
                title = board.name ?: "Issue board ${board.id}",
                meta = "Backlog ${if (board.hideBacklogList) "hidden" else "visible"} - Closed ${if (board.hideClosedList) "hidden" else "visible"}"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupScreen(
    state: LoadState<GroupData>,
    onBack: () -> Unit,
    onProjectClick: (GitLabProject) -> Unit,
    onGroupClick: (GitLabGroup) -> Unit,
    onIssueClick: (GitLabIssue) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Projects", "Subgroups", "Issues")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Outlined.Folder
                                    1 -> Icons.Outlined.AccountTree
                                    else -> Icons.Outlined.TaskAlt
                                },
                                contentDescription = label
                            )
                        },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                LoadState.Loading -> item { LoadingBlock("Loading group projects") }
                is LoadState.Error -> item { ErrorBlock(state.message) }
                is LoadState.Success -> {
                    item {
                        ListCard(
                            icon = Icons.Outlined.AccountTree,
                            title = state.value.group.name,
                            meta = state.value.group.fullPath
                        )
                    }
                    when (tab) {
                        0 -> projectItems(state.value.projects, onProjectClick)
                        1 -> groupItems(state.value.subgroups, onGroupClick)
                        2 -> issueItems(state.value.issues, "This group has no open issues.", onIssueClick)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkDetailScreen(
    state: LoadState<WorkDetailData>,
    onBack: () -> Unit,
    onComment: (String) -> Unit,
    onUpdateIssue: (Boolean, String) -> Unit
) {
    var comment by remember { mutableStateOf("") }
    var labels by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversation", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                LoadState.Loading -> item { LoadingBlock("Loading conversation") }
                is LoadState.Error -> item { ErrorBlock(state.message) }
                is LoadState.Success -> {
                    val data = state.value
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    data.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(data.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MetricChip(Icons.Outlined.Tag, data.state)
                                    data.labels.forEach { MetricChip(Icons.Outlined.Tag, it) }
                                }
                            }
                        }
                    }
                    if (data.target is DetailTarget.Issue) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = labels.ifBlank { data.labels.joinToString(",") },
                                    onValueChange = { labels = it },
                                    label = { Text("Labels, comma separated") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { onUpdateIssue(false, labels.ifBlank { data.labels.joinToString(",") }) }) {
                                        Text("Reopen")
                                    }
                                    Button(onClick = { onUpdateIssue(true, labels.ifBlank { data.labels.joinToString(",") }) }) {
                                        Text("Close / Save")
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = comment,
                                onValueChange = { comment = it },
                                label = { Text("Add a comment") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    onComment(comment)
                                    comment = ""
                                },
                                enabled = comment.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Comment")
                            }
                        }
                    }
                    if (data.notes.isEmpty()) {
                        item { EmptyBlock("No conversation history yet.") }
                    } else {
                        items(data.notes, key = { it.id }) { note ->
                            ListCard(
                                icon = Icons.Outlined.History,
                                title = note.author?.name ?: note.author?.username ?: "GitLab",
                                meta = note.createdAt ?: ""
                            ) {
                                Text(note.body)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeBlock(data: DashboardData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Your work", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Welcome, ${data.user.name}. Browse all accessible projects, groups, assigned work, reviews, to-dos, and recent activity.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProjectCard(project: GitLabProject, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProjectAvatar(project.name)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(project.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(project.pathWithNamespace, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            project.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricChip(Icons.Outlined.Star, "${project.starCount}")
                MetricChip(Icons.Outlined.AccountTree, "${project.forksCount}")
                MetricChip(Icons.Outlined.TaskAlt, "${project.openIssuesCount} open")
                project.defaultBranch?.let { MetricChip(Icons.Outlined.Tag, it) }
            }
        }
    }
}

@Composable
private fun ProjectHero(project: GitLabProject, branchCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                project.pathWithNamespace,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                project.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            project.description?.takeIf { it.isNotBlank() }?.let { Text(it) }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(Icons.Outlined.TaskAlt, "${project.openIssuesCount} issues")
                MetricChip(Icons.AutoMirrored.Outlined.MergeType, "$branchCount branches")
                MetricChip(Icons.Outlined.Star, "${project.starCount} stars")
            }
        }
    }
}

@Composable
private fun IssueRow(issue: GitLabIssue, onClick: () -> Unit = {}) {
    ListCard(icon = Icons.Outlined.TaskAlt, title = "#${issue.iid} ${issue.title}", meta = "${issue.state} by ${issue.author?.username ?: "unknown"}", onClick = onClick) {
        LabelRow(issue.labels)
    }
}

@Composable
private fun MergeRequestRow(mr: GitLabMergeRequest, onClick: () -> Unit = {}) {
    ListCard(
        icon = Icons.AutoMirrored.Outlined.MergeType,
        title = "!${mr.iid} ${mr.title}",
        meta = "${mr.sourceBranch} into ${mr.targetBranch}",
        onClick = onClick
    ) {
        Text(mr.mergeStatus ?: mr.state, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CommitRow(commit: GitLabCommit) {
    ListCard(
        icon = Icons.Outlined.History,
        title = commit.title,
        meta = "${commit.shortId} by ${commit.authorName}"
    )
}

@Composable
private fun ListCard(icon: ImageVector, title: String, meta: String, onClick: (() -> Unit)? = null, content: @Composable () -> Unit = {}) {
    val modifier = if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, overflow = TextOverflow.Ellipsis)
                Text(meta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                content()
            }
        }
    }
}

@Composable
private fun LabelRow(labels: List<String>) {
    if (labels.isEmpty()) return
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        labels.take(4).forEach { label ->
            AssistChip(onClick = {}, label = { Text(label) })
        }
    }
}

@Composable
private fun ProjectAvatar(name: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MetricChip(icon: ImageVector, text: String) {
    AssistChip(
        onClick = {},
        leadingIcon = { Icon(icon, null, Modifier.size(16.dp)) },
        label = { Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier.widthIn(min = 0.dp, max = 180.dp)
    )
}

@Composable
private fun LoadingBlock(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorBlock(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.ErrorOutline, null, tint = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(Modifier.width(10.dp))
            Text(message, color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}

@Composable
private fun EmptyBlock(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Outlined.Folder, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
