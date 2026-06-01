@file:OptIn(ExperimentalLayoutApi::class)

package com.example.jetlab.ui

import android.net.Uri
import android.util.Base64
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.jetlab.data.AppSettings
import com.example.jetlab.data.GitLabBoard
import com.example.jetlab.data.GitLabCommit
import com.example.jetlab.data.GitLabEvent
import com.example.jetlab.data.GitLabGroup
import com.example.jetlab.data.GitLabIssue
import com.example.jetlab.data.GitLabMergeRequest
import com.example.jetlab.data.GitLabProject
import com.example.jetlab.data.GitLabSession
import com.example.jetlab.data.GitLabTodo
import com.example.jetlab.data.GitLabUser
import com.example.jetlab.ui.theme.LabroxyTheme
import kotlinx.coroutines.launch

private enum class Screen { Loading, SignIn, Dashboard, Project, Group, Detail, User, About }

private enum class WorkSection(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Outlined.Home),
    Projects("Projects", Icons.Outlined.Folder),
    Groups("Groups", Icons.Outlined.AccountTree),
    Assigned("Assigned", Icons.Outlined.TaskAlt),
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
    val userState by viewModel.userState.collectAsState()
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
                        screen = Screen.Detail
                    },
                    settings = settings,
                    onThemeModeChange = viewModel::setThemeMode,
                    onPushNotificationsChange = viewModel::setPushNotifications,
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
                    onBack = { screen = Screen.Dashboard },
                    onIssueReferenceClick = viewModel::loadIssueReference,
                    onComment = viewModel::addComment,
                    onUpdateIssue = viewModel::updateIssueStatusAndLabels,
                    onUserClick = { username ->
                        viewModel.loadUser(username)
                        screen = Screen.User
                    }
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
    var tokenVisible by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.7f),
        focusedLabelColor = colorScheme.primary,
        cursorColor = colorScheme.primary,
        focusedLeadingIconColor = colorScheme.primary,
        unfocusedLeadingIconColor = colorScheme.onSurfaceVariant,
        focusedTrailingIconColor = colorScheme.primary,
        unfocusedTrailingIconColor = colorScheme.onSurfaceVariant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        colorScheme.primary.copy(alpha = 0.18f),
                        colorScheme.background,
                        colorScheme.secondary.copy(alpha = 0.14f)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountTree,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(34.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Labroxy",
                    style = MaterialTheme.typography.displaySmall,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "A calm GitLab cockpit for projects, groups, issues, reviews, and notifications.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(30.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.24f)),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.96f))
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("GitLab host") },
                    leadingIcon = { Icon(Icons.Outlined.Code, null) },
                    singleLine = true,
                    supportingText = { Text("Use gitlab.com or your self-managed GitLab URL.") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Personal access token") },
                    leadingIcon = { Icon(Icons.Outlined.Key, null) },
                    trailingIcon = {
                        IconButton(onClick = { tokenVisible = !tokenVisible }) {
                            Icon(
                                imageVector = if (tokenVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (tokenVisible) "Hide token" else "Show token"
                            )
                        }
                    },
                    singleLine = true,
                    supportingText = { Text("Stored only on this device and sent to your GitLab host.") },
                    visualTransformation = if (tokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onConnect(host, token) },
                    enabled = host.isNotBlank() && token.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Connect", fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Works with GitLab.com and self-managed instances",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
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
    onAboutClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var assignedTab by remember { mutableIntStateOf(0) }

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
                            WorkSection.Assigned -> assignedItems(
                                data = data,
                                query = query,
                                selectedTab = assignedTab,
                                onTabChange = { assignedTab = it },
                                onClick = onIssueClick
                            )
                            WorkSection.MergeRequests -> mrItems(data.assignedMergeRequests.filteredMergeRequests(query), onMergeRequestClick)
                            WorkSection.Todos -> todoItems(data.todos.filteredTodos(query), onTodoClick)
                            WorkSection.Notifications -> eventItems(data.events.filteredEvents(query), onEventClick)
                            WorkSection.Settings -> item {
                                SettingsScreen(
                                    settings = settings,
                                    onThemeModeChange = onThemeModeChange,
                                    onPushNotificationsChange = onPushNotificationsChange,
                                    onAboutClick = onAboutClick
                                )
                            }
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

@Composable
private fun SettingsScreen(
    settings: AppSettings,
    onThemeModeChange: (String) -> Unit,
    onPushNotificationsChange: (Boolean) -> Unit,
    onAboutClick: () -> Unit
) {
    val context = LocalContext.current
    val appName = remember {
        runCatching {
            context.applicationInfo.loadLabel(context.packageManager).toString()
        }.getOrDefault("Labroxy")
    }
    
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var cacheSize by remember { mutableStateOf("1.2 MB") }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    
    var syncInterval by remember { mutableStateOf("15 mins") }
    var showIntervalDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- APPEARANCE ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.Palette, title = "Appearance")
                
                Text(
                    "Theme Mode",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val themes = listOf(
                        Triple("system", "System", Icons.Outlined.SettingsSuggest),
                        Triple("light", "Light", Icons.Outlined.WbSunny),
                        Triple("dark", "Dark", Icons.Outlined.DarkMode)
                    )
                    themes.forEach { (value, label, icon) ->
                        ThemeOptionCard(
                            label = label,
                            icon = icon,
                            selected = settings.themeMode == value,
                            onClick = { onThemeModeChange(value) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- NOTIFICATIONS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.NotificationsActive, title = "Notifications")
                
                // Push Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Push Notifications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Allow $appName to notify you about GitLab updates.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = settings.pushNotifications,
                        onCheckedChange = onPushNotificationsChange
                    )
                }

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                // Sound Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Notification Sound", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Play sound for comments, issues, and MR activities.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                }

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                // Vibration Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Haptic Feedback", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Vibrate device on important notification updates.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
            }
        }

        // --- DATA & SYNC ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.Storage, title = "Data & Synchronization")
                
                // Offline Sync Dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Sync Frequency", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Background check rate for activities", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Box {
                        TextButton(onClick = { showIntervalDropdown = true }) {
                            Text(syncInterval, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, Modifier.size(16.dp))
                        }
                        
                        androidx.compose.material3.DropdownMenu(
                            expanded = showIntervalDropdown,
                            onDismissRequest = { showIntervalDropdown = false }
                        ) {
                            listOf("Manual", "15 mins", "30 mins", "1 hour").forEach { interval ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text(interval) },
                                    onClick = {
                                        syncInterval = interval
                                        showIntervalDropdown = false
                                        Toast.makeText(context, "Sync frequency updated to $interval", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                // Cache size and clear button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Offline Local Cache", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Cache size: $cacheSize",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = { showClearCacheDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Outlined.Cached, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Clear", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- ABOUT SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.Info, title = "About")
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onAboutClick)
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("About $appName", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("App details, version and technologies", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Go to About page",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showClearCacheDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Clear Cached Data") },
            text = { Text("Are you sure you want to clear all offline issue history, user avatars, and cached workspaces? The app will reload fresh data from your GitLab instance.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCacheDialog = false
                        cacheSize = "0.0 KB"
                        Toast.makeText(context, "Local cache successfully cleared!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ThemeOptionCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val appName = remember {
        runCatching {
            context.applicationInfo.loadLabel(context.packageManager).toString()
        }.getOrDefault("Labroxy")
    }
    
    val appIconDrawable = remember {
        runCatching {
            context.packageManager.getApplicationIcon(context.packageName)
        }.getOrNull()
    }
    
    val packageInfo = remember {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
        }.getOrNull()
    }
    val versionName = packageInfo?.versionName ?: "1.0.0"
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo?.longVersionCode ?: 1L
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toLong() ?: 1L
    }

    var clickCount by remember { mutableStateOf(0) }
    var showEasterEgg by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About $appName", fontWeight = FontWeight.SemiBold) },
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
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            clickCount++
                            if (clickCount >= 7) {
                                showEasterEgg = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (appIconDrawable != null) {
                        AsyncImage(
                            model = appIconDrawable,
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Version $versionName (Build $versionCode)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showEasterEgg) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Star, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "🚀 You found the $appName Easter Egg! Developer mode unlocked.",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "What is $appName?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "$appName is a premium, high-performance client for GitLab designed natively in Jetpack Compose with modern Material Design 3 guidelines.\n\n" +
                                "It enables developers to track issues, approve merge requests, manage todo activities, and stay up to date with automated notifications on the go, offline, and in real time.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                SettingsSectionHeader(icon = Icons.Outlined.Code, title = "Technology Stack")
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val techs = listOf("Kotlin", "Jetpack Compose", "Material 3", "DataStore", "Coroutines", "Flow", "Coil Image Loader", "Ktor/Serialization")
                    techs.forEach { tech ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tech) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Code,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "© 2026 JetLab Project. Open source Apache 2.0 License.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 16.dp)
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

private fun androidx.compose.foundation.lazy.LazyListScope.assignedItems(
    data: DashboardData,
    query: String,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    onClick: (GitLabIssue) -> Unit
) {
    item {
        val openCount = data.assignedWorkItems.size
        val completedCount = data.assignedCompletedWorkItems.size

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { onTabChange(0) },
                label = { Text("Open $openCount") }
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { onTabChange(1) },
                label = { Text("Completed $completedCount") }
            )
        }
    }

    val issues = if (selectedTab == 0) {
        data.assignedWorkItems.filteredIssues(query)
    } else {
        data.assignedCompletedWorkItems.filteredIssues(query)
    }

    if (issues.isEmpty()) {
        item { EmptyBlock(if (selectedTab == 0) "Nothing is assigned to you." else "No completed assigned issues yet.") }
    } else {
        items(issues, key = { it.id }) { issue ->
            IssueRow(issue, onClick = { onClick(issue) })
        }
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
                user = todo.author,
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
                user = event.author,
                title = event.targetTitle ?: event.targetType ?: "GitLab activity",
                meta = "${event.author?.username ?: "Someone"} ${event.displayAction}",
                onClick = { onClick(event) }
            )
        }
    }
}

private data class CommentAttachment(
    val url: String,
    val label: String,
    val isImage: Boolean
)

private data class IssueReference(
    val projectPath: String?,
    val projectId: Long,
    val issueIid: Long,
    val label: String
)

@Composable
private fun NoteBody(
    body: String,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onUserClick: (String) -> Unit
) {
    val attachments = remember(body, session.host, detail.webUrl) {
        extractCommentAttachments(body, session.host, detail.webUrl)
    }
    val displayBody = remember(body) { body.stripPreviewedMarkdownImages() }
    val issueReferences = remember(body, detail.target, detail.webUrl) {
        extractIssueReferences(body, detail)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (displayBody.isNotBlank()) {
            ClickableCommentText(
                text = displayBody,
                onUserClick = onUserClick
            )
        }
        attachments.filter { it.isImage }.forEach { attachment ->
            CommentImagePreview(attachment, session.token)
        }
        val files = attachments.filterNot { it.isImage }
        if (files.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                files.forEach { attachment ->
                    CommentFileChip(attachment)
                }
            }
        }
        if (issueReferences.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                issueReferences.forEach { reference ->
                    AssistChip(
                        onClick = {
                            onIssueReferenceClick(
                                reference.projectPath,
                                reference.projectId,
                                reference.issueIid
                            )
                        },
                        leadingIcon = { Icon(Icons.Outlined.TaskAlt, null, Modifier.size(16.dp)) },
                        label = { Text(reference.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentImagePreview(attachment: CommentAttachment, token: String) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val request = remember(attachment.url, token) {
        val builder = ImageRequest.Builder(context)
            .data(attachment.url)
            .addHeader("PRIVATE-TOKEN", token)
            .setHeader("Authorization", token.toGitLabBasicAuthHeader())
            .crossfade(true)
        if (attachment.url.isSvgUrl()) {
            builder.decoderFactory(SvgDecoder.Factory())
        }
        builder.build()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(attachment.url) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = request,
            contentDescription = attachment.label,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 280.dp)
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

@Composable
private fun CommentFileChip(attachment: CommentAttachment) {
    val uriHandler = LocalUriHandler.current

    AssistChip(
        onClick = { uriHandler.openUri(attachment.url) },
        leadingIcon = { Icon(Icons.Outlined.InsertDriveFile, null, Modifier.size(16.dp)) },
        label = { Text(attachment.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier.widthIn(max = 220.dp)
    )
}

private fun extractCommentAttachments(body: String, host: String, detailWebUrl: String?): List<CommentAttachment> {
    val markdownUrls = markdownLinkPattern.findAll(body).mapNotNull { it.groups[1]?.value }
    val bareUrls = bareUrlPattern.findAll(body).map { it.value.trimEnd('.', ',', ')') }
    return (markdownUrls + bareUrls)
        .mapNotNull { raw -> resolveAttachmentUrl(raw, host, detailWebUrl) }
        .distinct()
        .map { url ->
            val normalizedUrl = url.normalizeHttpUrl()
            CommentAttachment(
                url = normalizedUrl,
                label = url.substringBefore('?').substringBefore('#').substringAfterLast('/').ifBlank { "Attachment" },
                isImage = normalizedUrl.isPreviewableImageUrl()
            )
        }
        .toList()
}

private val markdownLinkPattern = Regex("""!?\[[^\]]*]\(([^)\s]+)(?:\s+"[^"]*")?\)""")
private val markdownImagePattern = Regex("""!\[[^\]]*]\([^)]+\)(?:\{[^}]*\})?""")
private val bareUrlPattern = Regex("""https?://[^\s)]+""")
private val sameProjectIssuePattern = Regex("""(?<![\w/])#(\d+)""")
private val crossProjectIssuePattern = Regex("""(?<![\w/.-])([A-Za-z0-9_.-]+(?:/[A-Za-z0-9_.-]+)+)#(\d+)""")
private val issueUrlPattern = Regex("""(?:https?://[^/\s)]+/)?([A-Za-z0-9_.-]+(?:/[A-Za-z0-9_.-]+)+)/-/issues/(\d+)""")

private fun String.stripPreviewedMarkdownImages(): String =
    replace(markdownImagePattern, "")
        .lines()
        .joinToString("\n") { it.trimEnd() }
        .trim()

private fun resolveAttachmentUrl(rawUrl: String, host: String, detailWebUrl: String?): String? {
    val cleaned = rawUrl.trim().trim('<', '>')
    if (cleaned.isBlank()) return null
    val projectBaseUrl = detailWebUrl?.toGitLabProjectBaseUrl()
    return when {
        cleaned.startsWith("http://") || cleaned.startsWith("https://") -> cleaned
        cleaned.startsWith("/uploads/") && projectBaseUrl != null -> projectBaseUrl + cleaned
        cleaned.startsWith("uploads/") && projectBaseUrl != null -> "$projectBaseUrl/$cleaned"
        cleaned.startsWith("/") -> host.trim().removeSuffix("/") + cleaned
        cleaned.startsWith("uploads/") -> host.trim().removeSuffix("/") + "/" + cleaned
        else -> null
    }
}

private fun String.normalizeHttpUrl(): String {
    val uri = runCatching { Uri.parse(this) }.getOrNull() ?: return this
    val scheme = uri.scheme ?: return this
    val authority = uri.encodedAuthority ?: return this
    if (scheme != "http" && scheme != "https") return this
    val encodedPath = Uri.encode(uri.path.orEmpty(), "/")
    val query = uri.encodedQuery?.let { "?$it" }.orEmpty()
    val fragment = uri.encodedFragment?.let { "#$it" }.orEmpty()
    return "$scheme://$authority$encodedPath$query$fragment"
}

private fun String.toGitLabBasicAuthHeader(): String {
    val credentials = "oauth2:$this".toByteArray()
    return "Basic ${Base64.encodeToString(credentials, Base64.NO_WRAP)}"
}

private fun extractIssueReferences(body: String, detail: WorkDetailData): List<IssueReference> {
    val projectId = (detail.target as? DetailTarget.Issue)?.projectId
        ?: (detail.target as? DetailTarget.MergeRequest)?.projectId
        ?: return emptyList()

    val linkedIssues = issueUrlPattern.findAll(body).mapNotNull { match ->
        val projectPath = match.groups[1]?.value ?: return@mapNotNull null
        val issueIid = match.groups[2]?.value?.toLongOrNull() ?: return@mapNotNull null
        IssueReference(
            projectPath = projectPath,
            projectId = projectId,
            issueIid = issueIid,
            label = "Open $projectPath#$issueIid"
        )
    }
    val crossProjectIssues = crossProjectIssuePattern.findAll(body).mapNotNull { match ->
        val projectPath = match.groups[1]?.value ?: return@mapNotNull null
        val issueIid = match.groups[2]?.value?.toLongOrNull() ?: return@mapNotNull null
        IssueReference(
            projectPath = projectPath,
            projectId = projectId,
            issueIid = issueIid,
            label = "Open $projectPath#$issueIid"
        )
    }
    val shorthandIssues = sameProjectIssuePattern.findAll(body).mapNotNull { match ->
        val issueIid = match.groups[1]?.value?.toLongOrNull() ?: return@mapNotNull null
        IssueReference(
            projectPath = null,
            projectId = projectId,
            issueIid = issueIid,
            label = "Open #$issueIid"
        )
    }

    return (linkedIssues + crossProjectIssues + shorthandIssues)
        .distinctBy { "${it.projectPath.orEmpty()}#${it.issueIid}" }
        .toList()
}

private fun String.toGitLabProjectBaseUrl(): String =
    substringBefore("/-/issues/")
        .substringBefore("/-/merge_requests/")
        .trimEnd('/')

private fun String.isPreviewableImageUrl(): Boolean {
    val path = substringBefore('?').substringBefore('#').lowercase()
    return listOf(".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg").any { path.endsWith(it) }
}

private fun String.isSvgUrl(): Boolean =
    substringBefore('?').substringBefore('#').lowercase().endsWith(".svg")

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
    session: GitLabSession,
    onBack: () -> Unit,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onComment: (String) -> Unit,
    onUpdateIssue: (Boolean, String) -> Unit,
    onUserClick: (String) -> Unit
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
                                user = note.author,
                                title = note.author?.name ?: note.author?.username ?: "GitLab",
                                meta = note.createdAt ?: ""
                            ) {
                                NoteBody(
                                    body = note.body,
                                    session = session,
                                    detail = data,
                                    onIssueReferenceClick = onIssueReferenceClick,
                                    onUserClick = onUserClick
                                )
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
    ListCard(
        icon = Icons.Outlined.TaskAlt,
        user = issue.author,
        title = "#${issue.iid} ${issue.title}",
        meta = "${issue.state} by ${issue.author?.username ?: "unknown"}",
        onClick = onClick
    ) {
        LabelRow(issue.labels)
    }
}

@Composable
private fun MergeRequestRow(mr: GitLabMergeRequest, onClick: () -> Unit = {}) {
    ListCard(
        icon = Icons.AutoMirrored.Outlined.MergeType,
        user = mr.author,
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
private fun ListCard(
    icon: ImageVector,
    title: String,
    meta: String,
    user: GitLabUser? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val modifier = if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            if (user != null) {
                UserAvatar(user = user, size = 36)
            } else {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
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

@Composable
private fun ClickableCommentText(
    text: String,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pattern = Regex("@([A-Za-z0-9_.-]+)")
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        pattern.findAll(text).forEach { result ->
            val matchRange = result.range
            val username = result.groups[1]!!.value
            
            if (matchRange.first > lastIndex) {
                append(text.substring(lastIndex, matchRange.first))
            }
            
            val start = length
            append("@$username")
            val end = length
            
            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                start = start,
                end = end
            )
            addStringAnnotation(
                tag = "USER",
                annotation = username,
                start = start,
                end = end
            )
            lastIndex = matchRange.last + 1
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
    
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "USER", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onUserClick(annotation.item)
                }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserScreen(
    state: LoadState<UserData>,
    onBack: () -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onMergeRequestClick: (GitLabMergeRequest) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Assigned Issues", "Merge Requests")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile", fontWeight = FontWeight.SemiBold) },
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
                                    else -> Icons.AutoMirrored.Outlined.MergeType
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
            is LoadState.Success -> {
                val data = state.value
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        UserHeroCard(data.user)
                    }
                    when (tab) {
                        0 -> issueItems(data.issues, "No open issues assigned to this user.", onIssueClick)
                        1 -> mrItems(data.mergeRequests, onMergeRequestClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserAvatar(user: GitLabUser, size: Int = 80) {
    var hasError by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .border(
                width = if (size > 50) 2.dp else 1.dp,
                color = if (size > 50) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!user.avatarUrl.isNullOrBlank() && !hasError) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.name,
                contentScale = ContentScale.Crop,
                onError = { hasError = true },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                user.name.take(1).uppercase(),
                color = Color.White,
                style = if (size > 50) {
                    MaterialTheme.typography.headlineLarge
                } else if (size > 36) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UserHeroCard(user: GitLabUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatar(user = user, size = 80)
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
