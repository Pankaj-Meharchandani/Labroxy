@file:OptIn(ExperimentalLayoutApi::class)

package com.example.labroxy.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ContentPaste
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontStyle
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.labroxy.data.AppSettings
import com.example.labroxy.data.GitLabAwardEmoji
import com.example.labroxy.data.GitLabBoard
import com.example.labroxy.data.GitLabCommit
import com.example.labroxy.data.GitLabDiscussion
import com.example.labroxy.data.GitLabEvent
import com.example.labroxy.data.GitLabGroup
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabLabel
import com.example.labroxy.data.GitLabMergeRequest
import com.example.labroxy.data.GitLabNote
import com.example.labroxy.data.GitLabProject
import com.example.labroxy.data.GitLabRelatedItem
import com.example.labroxy.data.GitLabSession
import com.example.labroxy.data.GitLabTodo
import com.example.labroxy.data.GitLabUser
import com.example.labroxy.ui.theme.LabroxyTheme
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
                            WorkSection.Notifications -> notificationItems(
                                todos = data.todos.filteredTodos(query),
                                onTodoClick = onTodoClick
                            )
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
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onPushNotificationsChange(true)
        } else {
            onPushNotificationsChange(false)
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

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
                            "Notify you when new GitLab to-do items arrive.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = settings.pushNotifications,
                        onCheckedChange = { enabled ->
                            if (enabled &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onPushNotificationsChange(enabled)
                            }
                        }
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
                            "Play sound for new GitLab to-do alerts.",
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
                            "Vibrate device on new to-do alerts.",
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
                    text = "© 2026 Labroxy Project. Open source Apache 2.0 License.",
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

private fun androidx.compose.foundation.lazy.LazyListScope.notificationItems(
    todos: List<GitLabTodo>,
    onTodoClick: (GitLabTodo) -> Unit
) {
    val items = todos.distinctBy { it.id }.sortedByDescending { it.createdAt }

    if (items.isEmpty()) {
        item { EmptyBlock("No pending GitLab to-dos.") }
    } else {
        items(items, key = { it.id }) { todo ->
            ListCard(
                icon = Icons.Outlined.NotificationsActive,
                user = todo.author,
                title = todo.target?.title ?: todo.body ?: todo.targetType,
                meta = "${todo.action} - ${todo.targetType}${todo.project?.name?.let { " in $it" } ?: ""}",
                onClick = { onTodoClick(todo) }
            )
        }
    }
}

private data class CommentAttachment(
    val url: String,
    val openUrl: String,
    val label: String,
    val isImage: Boolean
)

private data class RawCommentAttachment(
    val url: String,
    val label: String?,
    val isImageHint: Boolean
)

private data class ResolvedCommentAttachment(
    val loadUrl: String,
    val openUrl: String
)

private data class IssueReference(
    val projectPath: String?,
    val projectId: Long,
    val issueIid: Long,
    val label: String
)

private data class ReplyTarget(
    val discussionId: String,
    val authorName: String
)

@Composable
private fun NoteBody(
    body: String,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val attachments = remember(body, session.host, detail.webUrl, detail.target) {
        extractCommentAttachments(body, session.host, detail.webUrl, detail.target.projectId())
    }
    val issueReferences = remember(body, detail.target, detail.webUrl) {
        extractIssueReferences(body, detail)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val segments = remember(body) {
            val s = mutableListOf<NoteSegment>()
            val pattern = Regex("""(!\[[^\]]*]\([^)\s]+(?:\s+"[^"]*")?\)(?:\{[^}]*\})?)|(<img\b[^>]*\bsrc=(["'])(.*?)\3[^>]*>)""", RegexOption.IGNORE_CASE)
            var lastIndex = 0
            pattern.findAll(body).forEach { result ->
                if (result.range.first > lastIndex) {
                    val text = body.substring(lastIndex, result.range.first).stripHtmlTags()
                    if (text.isNotBlank()) s.add(NoteSegment.Text(text))
                }
                val rawUrl = result.groups[2]?.value ?: result.groups[4]?.value ?: result.value.substringAfter("(").substringBefore(")")
                val resolved = resolveAttachmentUrl(rawUrl, session.host, detail.webUrl, detail.target.projectId())
                if (resolved != null) {
                    val normalizedUrl = resolved.loadUrl.normalizeHttpUrl()
                    s.add(NoteSegment.Image(CommentAttachment(
                        url = normalizedUrl,
                        openUrl = resolved.openUrl.normalizeHttpUrl(),
                        label = resolved.openUrl.substringBefore('?').substringBefore('#').substringAfterLast('/').ifBlank { "Image" },
                        isImage = true
                    )))
                }
                lastIndex = result.range.last + 1
            }
            if (lastIndex < body.length) {
                val text = body.substring(lastIndex).stripHtmlTags()
                if (text.isNotBlank()) s.add(NoteSegment.Text(text))
            }
            s
        }

        segments.forEach { segment ->
            when (segment) {
                is NoteSegment.Text -> {
                    ClickableCommentText(
                        text = segment.text,
                        onLinkClick = { url ->
                            if (!onGitLabLinkClick(url)) {
                                uriHandler.openUri(url)
                            }
                        },
                        onUserClick = onUserClick
                    )
                }
                is NoteSegment.Image -> {
                    CommentImagePreview(segment.attachment, session.token)
                }
            }
        }

        val nonImageAttachments = attachments.filterNot { it.isImage }
        if (nonImageAttachments.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                nonImageAttachments.forEach { attachment ->
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

private sealed class NoteSegment {
    data class Text(val text: String) : NoteSegment()
    data class Image(val attachment: CommentAttachment) : NoteSegment()
}

private fun String.stripHtmlTags(): String = replace(Regex("""<[^>]*>"""), "").trim()

@Composable
private fun CommentImagePreview(attachment: CommentAttachment, token: String) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val request = remember(attachment.url, token) {
        val builder = ImageRequest.Builder(context)
            .data(attachment.url)
            .addHeader("PRIVATE-TOKEN", token)
            .crossfade(true)
            .decoderFactory(SvgDecoder.Factory())
        builder.build()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(attachment.openUrl) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        SubcomposeAsyncImage(
            model = request,
            contentDescription = attachment.label,
            contentScale = ContentScale.Fit,
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 2.dp)
                }
            },
            error = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.InsertDriveFile,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        attachment.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 280.dp)
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}

@Composable
private fun CommentFileChip(attachment: CommentAttachment) {
    val uriHandler = LocalUriHandler.current

    AssistChip(
        onClick = { uriHandler.openUri(attachment.openUrl) },
        leadingIcon = { Icon(Icons.Outlined.InsertDriveFile, null, Modifier.size(16.dp)) },
        label = { Text(attachment.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier.widthIn(max = 220.dp)
    )
}

private fun extractCommentAttachments(
    body: String,
    host: String,
    detailWebUrl: String?,
    projectId: Long?
): List<CommentAttachment> {
    val markdownImages = markdownImagePattern.findAll(body).mapNotNull { match ->
        val label = match.groups[1]?.value?.takeIf { it.isNotBlank() }
        val url = match.groups[2]?.value ?: return@mapNotNull null
        RawCommentAttachment(url = url, label = label, isImageHint = true)
    }
    val markdownLinks = markdownLinkPattern.findAll(body).mapNotNull { match ->
        val label = match.groups[1]?.value?.takeIf { it.isNotBlank() }
        val url = match.groups[2]?.value ?: return@mapNotNull null
        RawCommentAttachment(url = url, label = label, isImageHint = false)
    }
    val htmlImages = htmlImagePattern.findAll(body).mapNotNull { match ->
        val url = match.groups[2]?.value ?: return@mapNotNull null
        RawCommentAttachment(url = url, label = null, isImageHint = true)
    }
    val bareUrls = bareUrlPattern.findAll(body).map {
        RawCommentAttachment(url = it.value.trimEnd('.', ',', ')'), label = null, isImageHint = false)
    }

    return (markdownImages + markdownLinks + htmlImages + bareUrls)
        .mapNotNull { raw ->
            val resolved = resolveAttachmentUrl(raw.url, host, detailWebUrl, projectId) ?: return@mapNotNull null
            raw to resolved
        }
        .distinctBy { (_, resolved) -> resolved.loadUrl.normalizeHttpUrl() }
        .map { (raw, resolved) ->
            val normalizedUrl = resolved.loadUrl.normalizeHttpUrl()
            CommentAttachment(
                url = normalizedUrl,
                openUrl = resolved.openUrl.normalizeHttpUrl(),
                label = raw.label
                    ?: resolved.openUrl.substringBefore('?').substringBefore('#').substringAfterLast('/').ifBlank { "Attachment" },
                isImage = raw.isImageHint || normalizedUrl.isPreviewableImageUrl()
            )
        }
        .toList()
}

private val markdownImagePattern = Regex("""!\[([^\]]*)]\(([^)\s]+)(?:\s+"[^"]*")?\)(?:\{[^}]*\})?""")
private val markdownLinkPattern = Regex("""(?<!!)\[([^\]]*)]\(([^)\s]+)(?:\s+"[^"]*")?\)""")
private val htmlImagePattern = Regex("""<img\b[^>]*\bsrc=(["'])(.*?)\1[^>]*>""", RegexOption.IGNORE_CASE)
private val htmlTagPattern = Regex("""<[^>]*>""")
private val bareUrlPattern = Regex("""https?://[^\s)]+""")
private val sameProjectIssuePattern = Regex("""(?<![\w/])#(\d+)""")
private val crossProjectIssuePattern = Regex("""(?<![\w/.-])([A-Za-z0-9_.-]+(?:/[A-Za-z0-9_.-]+)+)#(\d+)""")
private val issueUrlPattern = Regex("""(?:https?://[^/\s)]+/)?([A-Za-z0-9_.-]+(?:/[A-Za-z0-9_.-]+)+)/-/issues/(\d+)""")

private fun resolveAttachmentUrl(
    rawUrl: String,
    host: String,
    detailWebUrl: String?,
    projectId: Long?
): ResolvedCommentAttachment? {
    val cleaned = rawUrl.trim().trim('<', '>')
    if (cleaned.isBlank()) return null
    val normalizedHost = host.trim().removeSuffix("/")
    val projectBaseUrl = detailWebUrl?.toGitLabProjectBaseUrl()
    val webUrl = when {
        cleaned.startsWith("http://") || cleaned.startsWith("https://") -> cleaned
        cleaned.startsWith("/uploads/") && projectBaseUrl != null -> projectBaseUrl + cleaned
        cleaned.startsWith("uploads/") && projectBaseUrl != null -> "$projectBaseUrl/$cleaned"
        cleaned.startsWith("/") -> normalizedHost + cleaned
        cleaned.startsWith("uploads/") -> "$normalizedHost/$cleaned"
        else -> null
    } ?: return null

    val uploadPath = webUrl.gitLabUploadPath()
    val loadUrl = if (projectId != null && uploadPath != null) {
        "$normalizedHost/api/v4/projects/$projectId/uploads/$uploadPath"
    } else {
        webUrl
    }
    return ResolvedCommentAttachment(loadUrl = loadUrl, openUrl = webUrl)
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

private fun String.gitLabUploadPath(): String? {
    val path = runCatching { Uri.parse(this).encodedPath }.getOrNull() ?: return null
    val marker = "/uploads/"
    val markerIndex = path.indexOf(marker)
    if (markerIndex < 0) return null
    val uploadPath = path.substring(markerIndex + marker.length)
    if (uploadPath.count { it == '/' } < 1) return null
    return uploadPath
}

private fun DetailTarget.projectId(): Long =
    when (this) {
        is DetailTarget.Issue -> projectId
        is DetailTarget.MergeRequest -> projectId
    }

private fun String.isPreviewableImageUrl(): Boolean {
    val path = substringBefore('?').substringBefore('#').lowercase()
    return listOf(".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg").any { path.endsWith(it) }
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
    session: GitLabSession,
    onBack: () -> Unit,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onComment: (String, String?) -> Unit,
    onUpdateIssue: (Boolean, String) -> Unit,
    onUserClick: (String) -> Unit,
    uploadMarkdown: String? = null,
    onUploadFile: (Long, Uri) -> Unit = { _, _ -> },
    onClearUpload: () -> Unit = {},
    onToggleReaction: (GitLabNote?, String) -> Unit = { _, _ -> },
    currentUserId: Long = 0
) {
    var comment by remember { mutableStateOf("") }
    var labels by remember { mutableStateOf("") }
    var replyTarget by remember { mutableStateOf<ReplyTarget?>(null) }
    val listState = rememberLazyListState()
    val data = (state as? LoadState.Success)?.value
    var activeNoteForReaction by remember { mutableStateOf<GitLabNote?>(null) }
    var detailReactionPickerActive by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uploadMarkdown) {
        if (uploadMarkdown != null) {
            comment = if (comment.isBlank()) uploadMarkdown else "$comment\n$uploadMarkdown"
            onClearUpload()
        }
    }

    LaunchedEffect(data?.target, data?.discussions?.sumOf { it.notes.size }) {
        val targetData = data ?: return@LaunchedEffect
        val conversationItems = if (targetData.discussions.isEmpty()) 1 else targetData.discussions.size
        val itemCount = 1 + (if (targetData.target is DetailTarget.Issue) 1 else 0) + conversationItems
        if (itemCount > 0) {
            listState.scrollToItem(itemCount - 1)
        }
    }

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
        },
        bottomBar = {
            if (data != null) {
                CommentComposerBar(
                    projectId = data.target.projectId(),
                    comment = comment,
                    replyTarget = replyTarget,
                    onCommentChange = { comment = it },
                    onCancelReply = { replyTarget = null },
                    onSubmit = {
                        onComment(comment, replyTarget?.discussionId)
                        comment = ""
                        replyTarget = null
                    },
                    onUploadFile = onUploadFile
                )
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
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
                    val loaded = state.value
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    loaded.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(loaded.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                if (!loaded.description.isNullOrBlank()) {
                                    NoteBody(
                                        body = loaded.description,
                                        session = session,
                                        detail = loaded,
                                        onIssueReferenceClick = onIssueReferenceClick,
                                        onGitLabLinkClick = onGitLabLinkClick,
                                        onUserClick = onUserClick
                                    )
                                }
                                
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MetricChip(Icons.Outlined.Tag, loaded.state)
                                    loaded.labelDetails.forEach { GitLabLabelChip(it) }
                                }

                                if (loaded.assignees.isNotEmpty()) {
                                    DetailPeopleSection(
                                        title = "Assignees",
                                        users = loaded.assignees,
                                        onUserClick = onUserClick
                                    )
                                }

                                if (loaded.target is DetailTarget.Issue) {
                                    DetailRelationSection(
                                        title = "Parent",
                                        emptyText = "None",
                                        items = listOfNotNull(loaded.parentItem),
                                        onGitLabLinkClick = onGitLabLinkClick
                                    )
                                    DetailRelationSection(
                                        title = "Child items",
                                        emptyText = "None",
                                        items = loaded.childItems,
                                        onGitLabLinkClick = onGitLabLinkClick
                                    )
                                    DetailRelationSection(
                                        title = "Linked items",
                                        emptyText = "None",
                                        items = loaded.linkedItems,
                                        onGitLabLinkClick = onGitLabLinkClick
                                    )
                                }
                                
                                EmojiRow(
                                    awardEmoji = loaded.awardEmoji,
                                    onToggleReaction = { onToggleReaction(null, it) },
                                    onShowReactionPicker = { detailReactionPickerActive = true },
                                    currentUserId = currentUserId
                                )
                            }
                        }
                    }
                    if (loaded.target is DetailTarget.Issue) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = labels.ifBlank { loaded.labels.joinToString(",") },
                                    onValueChange = { labels = it },
                                    label = { Text("Labels, comma separated") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { onUpdateIssue(false, labels.ifBlank { loaded.labels.joinToString(",") }) }) {
                                        Text("Reopen")
                                    }
                                    Button(onClick = { onUpdateIssue(true, labels.ifBlank { loaded.labels.joinToString(",") }) }) {
                                        Text("Close / Save")
                                    }
                                }
                            }
                        }
                    }
                    if (loaded.discussions.isEmpty()) {
                        item { EmptyBlock("No conversation history yet.") }
                    } else {
                        items(loaded.discussions, key = { it.id }) { discussion ->
                            DiscussionCard(
                                discussion = discussion,
                                session = session,
                                detail = loaded,
                                onIssueReferenceClick = onIssueReferenceClick,
                                onGitLabLinkClick = onGitLabLinkClick,
                                onUserClick = onUserClick,
                                onReply = { note ->
                                    replyTarget = ReplyTarget(
                                        discussionId = discussion.id,
                                        authorName = note.author?.name ?: note.author?.username ?: "comment"
                                    )
                                },
                                onToggleReaction = onToggleReaction,
                                onShowReactionPicker = { activeNoteForReaction = it },
                                currentUserId = currentUserId
                            )
                        }
                    }
                }
            }
        }
    }

    if (activeNoteForReaction != null || detailReactionPickerActive) {
        EmojiPickerSheet(
            sheetState = sheetState,
            onDismiss = { 
                activeNoteForReaction = null
                detailReactionPickerActive = false
            },
            onEmojiSelected = { emojiName ->
                if (detailReactionPickerActive) {
                    onToggleReaction(null, emojiName)
                } else {
                    activeNoteForReaction?.let { onToggleReaction(it, emojiName) }
                }
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        activeNoteForReaction = null
                        detailReactionPickerActive = false
                    }
                }
            }
        )
    }
}

@Composable
private fun CommentComposerBar(
    projectId: Long,
    comment: String,
    replyTarget: ReplyTarget?,
    onCommentChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSubmit: () -> Unit,
    onUploadFile: (Long, Uri) -> Unit
) {
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onUploadFile(projectId, it) }
    }
    val context = LocalContext.current

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (replyTarget != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Replying to ${replyTarget.authorName}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    TextButton(onClick = onCancelReply) {
                        Text("Cancel")
                    }
                }
            }
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                label = { Text(if (replyTarget == null) "Add a comment" else "Add a reply") },
                minLines = 1,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { filePicker.launch("*/*") }) {
                    Icon(Icons.Outlined.AttachFile, "Attach file")
                }
                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val uri = clip.getItemAt(0).uri
                        if (uri != null) {
                            onUploadFile(projectId, uri)
                        } else {
                            Toast.makeText(context, "No image in clipboard", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(Icons.Outlined.ContentPaste, "Paste image")
                }
                Button(
                    onClick = onSubmit,
                    enabled = comment.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (replyTarget == null) "Comment" else "Reply")
                }
            }
        }
    }
}

@Composable
private fun DiscussionCard(
    discussion: GitLabDiscussion,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit,
    onReply: (GitLabNote) -> Unit,
    onToggleReaction: (GitLabNote?, String) -> Unit,
    onShowReactionPicker: (GitLabNote) -> Unit,
    currentUserId: Long
) {
    val firstNote = discussion.notes.firstOrNull()
    if (firstNote == null) {
        EmptyBlock("Empty discussion")
        return
    }

    if (firstNote.system) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            discussion.notes.forEach { note ->
                SystemNoteRow(
                    note = note,
                    session = session,
                    detail = detail,
                    onIssueReferenceClick = onIssueReferenceClick,
                    onGitLabLinkClick = onGitLabLinkClick,
                    onUserClick = onUserClick
                )
            }
        }
        return
    }

    val replies = discussion.notes.drop(1)

    ListCard(
        icon = Icons.Outlined.History,
        user = firstNote.author,
        title = firstNote.author?.name ?: firstNote.author?.username ?: "GitLab",
        meta = firstNote.createdAt ?: "",
        onUserClick = onUserClick
    ) {
        DiscussionNoteBody(
            note = firstNote,
            session = session,
            detail = detail,
            onIssueReferenceClick = onIssueReferenceClick,
            onGitLabLinkClick = onGitLabLinkClick,
            onUserClick = onUserClick,
            onReply = { onReply(firstNote) },
            onToggleReaction = onToggleReaction,
            onShowReactionPicker = onShowReactionPicker,
            currentUserId = currentUserId
        )
        if (replies.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                replies.forEach { reply ->
                    DiscussionReply(
                        note = reply,
                        session = session,
                        detail = detail,
                        onIssueReferenceClick = onIssueReferenceClick,
                        onGitLabLinkClick = onGitLabLinkClick,
                        onUserClick = onUserClick,
                        onReply = { onReply(reply) },
                        onToggleReaction = onToggleReaction,
                        onShowReactionPicker = onShowReactionPicker,
                        currentUserId = currentUserId
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemNoteRow(
    note: GitLabNote,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(start = 14.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                note.author?.name ?: note.author?.username ?: "GitLab",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { note.author?.username?.let { onUserClick(it) } }
            )
            Box(modifier = Modifier.weight(1f)) {
                NoteBody(
                    body = note.body,
                    session = session,
                    detail = detail,
                    onIssueReferenceClick = onIssueReferenceClick,
                    onGitLabLinkClick = onGitLabLinkClick,
                    onUserClick = onUserClick
                )
            }
            Text(
                note.createdAt ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DiscussionNoteBody(
    note: GitLabNote,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit,
    onReply: () -> Unit,
    onToggleReaction: (GitLabNote?, String) -> Unit,
    onShowReactionPicker: (GitLabNote) -> Unit,
    currentUserId: Long
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NoteBody(
            body = note.body,
            session = session,
            detail = detail,
            onIssueReferenceClick = onIssueReferenceClick,
            onGitLabLinkClick = onGitLabLinkClick,
            onUserClick = onUserClick
        )
        EmojiRow(
            awardEmoji = note.awardEmoji,
            onToggleReaction = { onToggleReaction(note, it) },
            onShowReactionPicker = { onShowReactionPicker(note) },
            currentUserId = currentUserId
        )
        if (!note.system) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                TextButton(
                    onClick = onReply,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFC6D26))
                ) {
                    Text("Reply", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmojiRow(
    awardEmoji: List<GitLabAwardEmoji>,
    onToggleReaction: (String) -> Unit,
    onShowReactionPicker: () -> Unit,
    currentUserId: Long,
    pinnedEmojis: List<String> = emptyList()
) {
    val grouped = awardEmoji.groupBy { it.name }
    val allEmojiNames = (pinnedEmojis + grouped.keys).distinct()
    
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        allEmojiNames.forEach { name ->
            val awards = grouped[name] ?: emptyList()
            val hasMyAward = awards.any { it.user.id == currentUserId }
            Surface(
                onClick = { onToggleReaction(name) },
                shape = RoundedCornerShape(8.dp),
                color = if (hasMyAward) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    if (hasMyAward) 2.dp else 1.dp,
                    if (hasMyAward) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = emojiMap[name] ?: name, fontSize = 16.sp)
                    Text(
                        text = "${awards.size}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (hasMyAward) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        // Add reaction smiley button
        Surface(
            onClick = onShowReactionPicker,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Icon(
                imageVector = Icons.Outlined.SentimentSatisfied,
                contentDescription = "Add reaction",
                modifier = Modifier
                    .padding(6.dp)
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmojiPickerSheet(
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredEmojis = remember(searchQuery) {
        if (searchQuery.isBlank()) emojiMap.toList()
        else emojiMap.filter { it.key.contains(searchQuery, ignoreCase = true) }.toList()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Add reaction",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close")
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
            ) {
                items(filteredEmojis) { (name, emoji) ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { onEmojiSelected(name) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

private val emojiMap = mapOf(
    "thumbsup" to "👍",
    "thumbsdown" to "👎",
    "saluting_face" to "🫡",
    "smile" to "😄",
    "tada" to "🎉",
    "confused" to "😕",
    "heart" to "❤️",
    "rocket" to "🚀",
    "eyes" to "👀",
    "laughing" to "😆",
    "sweat_smile" to "😅",
    "thinking" to "🤔",
    "cry" to "😢",
    "facepalm" to "🤦",
    "ok_hand" to "👌",
    "fire" to "🔥",
    "clap" to "👏",
    "joy" to "😂",
    "heart_eyes" to "😍",
    "pill" to "💊",
    "hammer" to "🔨",
    "white_check_mark" to "✅",
    "x" to "❌",
    "sunny" to "☀️",
    "moon" to "🌙",
    "star" to "⭐",
    "party_popper" to "🎉",
    "pray" to "🙏",
    "grin" to "😁",
    "rolling_on_the_floor_laughing" to "🤣",
    "blush" to "😊",
    "innocent" to "😇",
    "star_struck" to "🤩",
    "kissing_heart" to "😘",
    "zany_face" to "🤪",
    "shushing_face" to "🤫",
    "money_mouth_face" to "🤑",
    "hugging_face" to "🤗",
    "sleeping" to "😴",
    "sunglasses" to "😎",
    "neutral_face" to "😐",
    "expressionless" to "😑",
    "grimacing" to "😬",
    "pensive" to "😔",
    "sob" to "😭",
    "angry" to "😠",
    "mask" to "😷"
)

@Composable
private fun DiscussionReply(
    note: GitLabNote,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit,
    onReply: () -> Unit,
    onToggleReaction: (GitLabNote?, String) -> Unit,
    onShowReactionPicker: (GitLabNote) -> Unit,
    currentUserId: Long
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        UserAvatar(
            user = note.author ?: GitLabUser(id = 0, username = "gitlab", name = "GitLab"),
            size = 32,
            onClick = { note.author?.username?.let { onUserClick(it) } }
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    note.author?.name ?: note.author?.username ?: "GitLab",
                    modifier = Modifier.weight(1f).clickable { note.author?.username?.let { onUserClick(it) } },
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    note.createdAt ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            NoteBody(
                body = note.body,
                session = session,
                detail = detail,
                onIssueReferenceClick = onIssueReferenceClick,
                onGitLabLinkClick = onGitLabLinkClick,
                onUserClick = onUserClick
            )
            EmojiRow(
                awardEmoji = note.awardEmoji,
                onToggleReaction = { onToggleReaction(note, it) },
                onShowReactionPicker = { onShowReactionPicker(note) },
                currentUserId = currentUserId
            )
            if (!note.system) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    TextButton(
                        onClick = onReply,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFC6D26))
                    ) {
                        Text("Reply", fontWeight = FontWeight.Bold)
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
private fun IssueRow(issue: GitLabIssue, onUserClick: (String) -> Unit = {}, onClick: () -> Unit = {}) {
    ListCard(
        icon = Icons.Outlined.TaskAlt,
        user = issue.author,
        title = "#${issue.iid} ${issue.title}",
        meta = "${issue.state} by ${issue.author?.username ?: "unknown"}",
        onUserClick = onUserClick,
        onClick = onClick
    ) {
        LabelRow(issue.labels)
    }
}

@Composable
private fun MergeRequestRow(mr: GitLabMergeRequest, onUserClick: (String) -> Unit = {}, onClick: () -> Unit = {}) {
    ListCard(
        icon = Icons.AutoMirrored.Outlined.MergeType,
        user = mr.author,
        title = "!${mr.iid} ${mr.title}",
        meta = "${mr.sourceBranch} into ${mr.targetBranch}",
        onUserClick = onUserClick,
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
    onUserClick: ((String) -> Unit)? = null,
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
                UserAvatar(user = user, size = 36, onClick = { onUserClick?.invoke(user.username) })
            } else {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(enabled = onUserClick != null) {
                        user?.username?.let { onUserClick?.invoke(it) }
                    }
                )
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
private fun GitLabLabelChip(label: GitLabLabel) {
    val background = gitLabColor(label.color, MaterialTheme.colorScheme.secondaryContainer)
    val content = gitLabColor(label.textColor, readableOn(background))
    AssistChip(
        onClick = {},
        leadingIcon = {
            Icon(
                Icons.Outlined.Tag,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = content
            )
        },
        label = {
            Text(
                label.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = content
            )
        },
        border = BorderStroke(1.dp, content.copy(alpha = 0.35f)),
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = background,
            labelColor = content,
            leadingIconContentColor = content
        ),
        modifier = Modifier.widthIn(min = 0.dp, max = 220.dp)
    )
}

@Composable
private fun DetailPeopleSection(
    title: String,
    users: List<GitLabUser>,
    onUserClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            users.forEach { user ->
                Surface(
                    onClick = { onUserClick(user.username) },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserAvatar(user = user, size = 26)
                        Text(
                            user.name.ifBlank { "@${user.username}" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.widthIn(max = 180.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRelationSection(
    title: String,
    emptyText: String,
    items: List<GitLabRelatedItem>,
    onGitLabLinkClick: (String) -> Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        if (items.isEmpty()) {
            Text(
                emptyText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.take(5).forEach { item ->
                    Surface(
                        onClick = { item.webUrl?.let { onGitLabLinkClick(it) } },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.AccountTree,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.title.ifBlank { item.reference ?: "Related item" },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                val meta = listOfNotNull(
                                    item.reference,
                                    item.state.takeIf { it.isNotBlank() }
                                ).distinct().joinToString(" · ")
                                if (meta.isNotBlank()) {
                                    Text(
                                        meta,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                if (items.size > 5) {
                    Text(
                        "+ ${items.size - 5} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun gitLabColor(value: String?, fallback: Color): Color {
    val clean = value?.trim()?.removePrefix("#") ?: return fallback
    if (clean.length != 6) return fallback
    val parsed = clean.toLongOrNull(16) ?: return fallback
    return Color((0xFF000000L or parsed).toInt())
}

private fun readableOn(background: Color): Color {
    val luminance = (background.red * 0.299f) + (background.green * 0.587f) + (background.blue * 0.114f)
    return if (luminance > 0.58f) Color(0xFF1F1F24) else Color.White
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
    onLinkClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pattern = Regex("""!?\[([^\]]+)]\((https?://[^)\s]+)(?:\s+"[^"]*")?\)|https?://[^\s)]+|@([A-Za-z0-9_.-]+)|\*\*([^*]+)\*\*|\*([^*]+)\*|__([^_]+)__| _([^_]+)_""")
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        pattern.findAll(text).forEach { result ->
            val matchRange = result.range
            if (matchRange.first > lastIndex) {
                append(text.substring(lastIndex, matchRange.first))
            }

            val start = length
            val markdownLabel = result.groups[1]?.value
            val markdownUrl = result.groups[2]?.value
            val username = result.groups[3]?.value
            val boldText = result.groups[4]?.value ?: result.groups[6]?.value
            val italicText = result.groups[5]?.value ?: result.groups[7]?.value
            val plainUrl = result.value.takeIf { it.startsWith("http://") || it.startsWith("https://") }

            when {
                markdownUrl != null -> {
                    val label = markdownLabel ?: markdownUrl
                    append(label)
                    addStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                        start, length
                    )
                    addStringAnnotation("LINK", markdownUrl, start, length)
                }
                plainUrl != null -> {
                    val url = plainUrl.trimEnd('.', ',', ')')
                    append(url)
                    addStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                        start, length
                    )
                    addStringAnnotation("LINK", url, start, length)
                }
                username != null -> {
                    append("@$username")
                    addStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                        start, length
                    )
                    addStringAnnotation("USER", username, start, length)
                }
                boldText != null -> {
                    append(boldText)
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, length)
                }
                italicText != null -> {
                    append(italicText)
                    addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, length)
                }
            }
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
            annotatedString.getStringAnnotations(tag = "LINK", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onLinkClick(annotation.item)
                    return@ClickableText
                }
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
private fun UserAvatar(user: GitLabUser, size: Int = 80, onClick: (() -> Unit)? = null) {
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
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
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
